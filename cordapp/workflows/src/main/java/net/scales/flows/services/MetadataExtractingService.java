package net.scales.flows.services;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.sf.saxon.TransformerFactoryImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CordaService
public class MetadataExtractingService extends SingletonSerializeAsToken {

    private final static Logger logger = LoggerFactory.getLogger(MetadataExtractingService.class);

    private final byte[] invoiceExtractor;
    private final byte[] sdiExtractor;

    public MetadataExtractingService(ServiceHub service) throws IOException {
        ClassLoader loader = getClass().getClassLoader();

        invoiceExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/invoice_extractor_0.0.1.xslt"));
        sdiExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/sdi_notification_extractor_0.0.1.xslt"));
    }

    /**
     * Extracts invoice metadata from file
     */
    public Map<String, String> getInvoiceMetadata(InputStream invoice) {
        return this.transform(invoice, new ByteArrayInputStream(invoiceExtractor));
    }

    /**
     * Extracts SDI notification metadata from file
     */
    public Map<String, String> getSdiNotificationMetadata(InputStream sdi) {
        return this.transform(sdi, new ByteArrayInputStream(sdiExtractor));
    }

    /**
     * Extracts metadata from file based on the extractor XSLT
     */
    private Map<String, String> transform(InputStream file, InputStream extractor) {
        try {
            TransformerFactory factory = new TransformerFactoryImpl();
            Transformer transformer = factory.newTransformer(new StreamSource(extractor));

            Source source = new StreamSource(file);
            DOMResult result = new DOMResult();

            transformer.transform(source, result);

            NodeList nodes = ((Document) result.getNode()).getDocumentElement().getChildNodes();

            Map<String, String> elements = new HashMap<>();

            int length = nodes.getLength();

            for (int i = 0; i < length; i++) {
                elements.put(nodes.item(i).getNodeName(), nodes.item(i).getTextContent());
            }

            return elements;

        } catch (TransformerException ex) {
            logger.error("", ex);

            return null;
        }
    }

}