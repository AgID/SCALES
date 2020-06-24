package net.scales.hub.tasks;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import net.corda.core.messaging.CordaRPCOps;
import net.scales.flows.IssueInvoiceFlow;
import net.scales.hub.server.NodeRPCConnection;

@Component
public class InvoiceTask {

    private final static Logger logger = LoggerFactory.getLogger(InvoiceTask.class);

    private final CordaRPCOps proxy;

    @Value("${sftp.dir}")
    private String dir;

    @Value("${vas.url}")
	private String vasUrl;

	@Value("${dispatcher.url}")
	private String dispatcherUrl;

    public InvoiceTask(NodeRPCConnection rpc) {
		this.proxy = rpc.proxy;
	}

    @Scheduled(fixedDelay = 5000)
    public void issueInvoiceToken() throws InterruptedException {
        File hub = Paths.get(dir).toAbsolutePath().toFile();

        // Loop all subfolders in sftp directory
        for (File ee : hub.listFiles()) {
            if (!ee.isDirectory()) {
                continue;
            }

            // Loop all subfolders in hub directory
            for (File input : ee.listFiles()) {
                if (!input.isDirectory() || !input.getName().equals("input")) {
                    continue;
                }

                // Loop all files in input directory
                for (File file : input.listFiles()) {
                    if (file.isDirectory()) {
                        continue;
                    }

                    ZipFile zip;

                    try {
                        // Throw exception when file is not zip
                        zip = new ZipFile(file);
            
                    } catch(Exception ex) {
                        continue;
                    }

                    logger.info("Start file {}", zip.getName());

                    List<String> errMsg = startIssuingFlow(hub.getName(), ee.getName(), zip);

                    String id = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    writeOutputFile(id, file, errMsg);
                    backupFile(id, file);

                    logger.info("Finish!");
                }
            }
        }
    }

    private List<String> startIssuingFlow(String hubId, String endEntityId, ZipFile zip) {
        List<String> errMsg = new ArrayList<>();

        try {
            logger.info("Validating file");

            int cnt = 0;
            byte[] invoice = null, sdi = null;

            // Loop all files in the zip file
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().toUpperCase().startsWith("SDI_")) {
                    sdi = IOUtils.toByteArray(zip.getInputStream(entry));

                } else {
                    invoice = IOUtils.toByteArray(zip.getInputStream(entry));
                }

                cnt++;
            }

            // Close the zip file
            zip.close();

            if (cnt > 2) {
                errMsg.add("The number of files in the zip file is " + cnt);
            }

            if (invoice == null) {
                errMsg.add("The zip file does not contain the invoice file");
            }

            if (sdi == null) {
                errMsg.add("The zip file does not contain the SDI notification file");
            }

            if (errMsg.size() == 0) {
                logger.info("Issuing token");

                // Start flow to issue new token
                String result = proxy.startFlowDynamic(IssueInvoiceFlow.class, hubId, endEntityId, invoice, sdi).getReturnValue().get();

                switch(result) {
                    case "0":
                        errMsg.add("The invoice file was minted");
                        break;

                    case "1":
                        errMsg.add("Can not extract metadata from the invoice file");
                        break;

                    case "2":
                        errMsg.add("Can not extract metadata from the SDI notification file");
                        break;

                    case "3":
                        errMsg.add("End entity uploaded invalid invoice");
                        break;

                    default:
                        // Split result into transaction id and invoice number
                        String[] arr = result.split("_", 2);

                        notifyToDispatcher(endEntityId, arr[1]);

                        logger.info("Transaction {}", arr[0]);
                }
            }

		} catch (Exception ex) {
			logger.error("", ex);

			errMsg.add(ex.getMessage());
        }

        return errMsg;
    }

    private void backupFile(String id, File origin) {
        try {
            logger.info("Backing up file");

            String target = origin.getParent() + "/bak/" + id + "_" + origin.getName();

            Files.move(Paths.get(origin.getAbsolutePath()), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private void writeOutputFile(String id, File origin, List<String> content) {
        try {
            logger.info("Writing output file");

            String fileName = (content.size() > 0 ? "KO-" : "OK-") + id + "_" + FilenameUtils.getBaseName(origin.getName()) + ".out";

            Files.write(Paths.get(origin.getParentFile().getParent() + "/output/" + fileName), content, Charset.defaultCharset());

        } catch (Exception ex) {
            logger.error("", ex);
        }
    }

    private void notifyToDispatcher(String endEntityId, String invoiceNumber) throws Exception {
		// Create params
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", vasUrl + "," + endEntityId + "," + invoiceNumber));

		HttpPost request = new HttpPost(dispatcherUrl);
		request.setEntity(new UrlEncodedFormEntity(params));

		// Make request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(request);

		if (response.getStatusLine().getStatusCode() != 200) {
			logger.error("", "Can not notify to the dispatcher for invoice " + invoiceNumber);
		}
    }

}