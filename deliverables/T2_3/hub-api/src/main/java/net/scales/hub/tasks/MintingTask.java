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
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.scales.flows.IssueInvoiceIndirectFlow;
import net.scales.flows.IssuePurchaseOrderIndirectFlow;
import net.scales.hub.server.NodeRPCConnection;
import net.scales.hub.utils.HttpUtils;

@Component
public class MintingTask {

    private final static Logger logger = LoggerFactory.getLogger(MintingTask.class);

    private final CordaRPCOps proxy;

    @Value("${sftp.dir}")
    private String dir;

    @Value("${vas.url}")
	private String vasUrl;

	@Value("${dispatcher.url}")
    private String dispatcherUrl;
    
    @Value("${sdi.name}")
	private String sdiName;

    @Value("${api.key}")
    private String apiKey;

    public MintingTask(NodeRPCConnection rpc) {
		this.proxy = rpc.proxy;
	}

    @Scheduled(fixedDelay = 5000)
    public void execute() throws InterruptedException {
        Party sdi = proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(sdiName));

        File hub = Paths.get(dir).toAbsolutePath().toFile();

        // Loops all subfolders in Hub directory
        for (File ee : hub.listFiles()) {
            if (!ee.isDirectory()) {
                continue;
            }

            // Loops all subfolders in End Entity directory
            for (File input : ee.listFiles()) {
                if (!input.isDirectory() || !input.getName().equals("input")) {
                    continue;
                }

                // Loops all files in input directory
                for (File file : input.listFiles()) {
                    if (file.isDirectory()) {
                        continue;
                    }

                    ZipFile zip;

                    try {
                        // Throws exception when file is not zip file
                        zip = new ZipFile(file);
            
                    } catch(Exception ex) {
                        continue;
                    }

                    logger.info("Executing file {}", zip.getName());

                    List<String> content = startMintingFlow(sdi, hub.getName(), ee.getName(), zip);

                    String id = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    writeOutputFile(id, file, content);

                    backupFile(id, file);
                }
            }
        }
    }

    private List<String> startMintingFlow(Party sdi, String hubId, String endEntityId, ZipFile zip) {
        List<String> msg = new ArrayList<>();

        try {
            logger.info("Validating file");

            String type = FilenameUtils.getBaseName(zip.getName()).toUpperCase().startsWith("INVOICE_") ? "invoice" : "order";

            int cnt = 0;
            byte[] file = null, notification = null;

            // Loops all files in the zip file
            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = entries.nextElement();

                String entryName = entry.getName().toUpperCase();

                if (entryName.startsWith("SDI_") || entryName.startsWith("NSO_")) {
                    notification = IOUtils.toByteArray(zip.getInputStream(entry));

                } else {
                    file = IOUtils.toByteArray(zip.getInputStream(entry));
                }

                cnt++;
            }

            // Closes the zip file
            zip.close();

            if (cnt > 2) {
                msg.add("The number of files in the zip file is " + cnt);
            }

            if (file == null) {
                msg.add("The zip file does not contain the " + type + " file");
            }

            if (notification == null) {
                msg.add("The zip file does not contain the notification file");
            }

            if (msg.size() == 0) {
                logger.info("Minting token");

                String result;

                // Starts flow to mint new token
                if (type.equals("invoice")) {
                    result = proxy.startFlowDynamic(IssueInvoiceIndirectFlow.class, hubId, endEntityId, file, notification, sdi).getReturnValue().get();

                } else {
                    result = proxy.startFlowDynamic(IssuePurchaseOrderIndirectFlow.class, hubId, endEntityId, file, notification, sdi).getReturnValue().get();
                }

                switch(result) {
                    case "ERR001":
                        msg.add("Can't extract metadata from the " + type + " file");
                        break;

                    case "ERR002":
                        msg.add("Can't extract metadata from the notification file");
                        break;

                    case "ERR003":
                        msg.add("Only supplier or buyer is allowed to mint " + type);
                        break;

                    case "ERR004":
                        msg.add("The " + type + " file was minted");
                        break;

                    default:
                        // Splits result into transaction id and invoice or order id
                        String[] elements = result.split("_", 2);

                        // Notifies for the dispatcher after minted invoice successfully
			            notifyToDispatcher(hubId, endEntityId, type, elements[1]);

                        logger.info("Transaction {}", elements[0]);
                }
            }

		} catch (Exception ex) {
			logger.error("", ex);

			msg.add(ex.getMessage());
        }

        return msg;
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

    private void notifyToDispatcher(String hubId, String endEntityId, String type, String id) {
		try {
			// Creates params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("data", id));
            params.add(new BasicNameValuePair("hubId", hubId));
			params.add(new BasicNameValuePair("endEntityId", endEntityId));
			params.add(new BasicNameValuePair("vasUrl", vasUrl));

			int status = HttpUtils.post(dispatcherUrl, params, apiKey);

			if (status != 200) {
				logger.info("Can't send notification to the dispatcher: " + type + "," + id);

			} else {
				logger.info("Sent notification to the dispatcher: " + vasUrl + "," + endEntityId + "," + type + "," + id);
			}

		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

}