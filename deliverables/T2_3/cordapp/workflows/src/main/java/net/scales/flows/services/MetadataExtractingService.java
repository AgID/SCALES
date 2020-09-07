package net.scales.flows.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CordaService
public class MetadataExtractingService extends SingletonSerializeAsToken {

    private final static Logger logger = LoggerFactory.getLogger(MetadataExtractingService.class);

    private final byte[] invoiceExtractor;
    private final byte[] invoiceNotificationExtractor;
    private final byte[] orderExtractor;
    private final byte[] orderNotificationExtractor;

    public MetadataExtractingService(ServiceHub service) throws IOException {
        ClassLoader loader = getClass().getClassLoader();

        invoiceExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/invoice_extractor_0.0.1.xslt"));
        invoiceNotificationExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/invoice_notification_extractor_0.0.1.xslt"));
        orderExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/order_extractor_0.0.2.xslt"));
        orderNotificationExtractor = IOUtils.toByteArray(loader.getResourceAsStream("extractors/order_notification_extractor_0.0.1.xslt"));
    }

    /**
     * Extracts invoice metadata from file
     */
    public Map<String, String> getInvoiceMetadata(InputStream invoice) {
        return this.transform(invoice, new ByteArrayInputStream(invoiceExtractor));
    }

    /**
     * Extracts invoice notification metadata from file
     */
    public Map<String, String> getInvoiceNotificationMetadata(InputStream notification) {
        return this.transform(notification, new ByteArrayInputStream(invoiceNotificationExtractor));
    }

    /**
     * Extracts order metadata from file
     */
    public Map<String, String> getOrderMetadata(InputStream order) {
        return this.transform(order, new ByteArrayInputStream(orderExtractor));
    }

    /**
     * Extracts order notification metadata from file
     */
    public Map<String, String> getOrderNotificationMetadata(InputStream notification) {
        return this.transform(notification, new ByteArrayInputStream(orderNotificationExtractor));
    }

    /**
     * Extracts metadata from file based on the extractor XSLT
     */
    private Map<String, String> transform(InputStream file, InputStream extractor) {
        try {
            Transformer transformer = (new TransformerFactoryImpl()).newTransformer(new StreamSource(extractor));

            DOMResult result = new DOMResult();

            transformer.transform(new StreamSource(file), result);

            Element root = ((Document) result.getNode()).getDocumentElement();

            NodeList nodes = root.getChildNodes();

            int len1 = nodes.getLength();

            Map<String, String> elements = new HashMap<>();

            if (!root.getNodeName().equals("Root")) {
                for (int i = 0; i < len1; i++) {
                    Node node = nodes.item(i);

                    elements.put(node.getNodeName(), node.getTextContent());
                }

                return elements;
            }

            for (int i = 0; i < len1; i++) {
                Node node = nodes.item(i);

                // Skips text and comment nodes
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                NodeList childs = node.getChildNodes();

                int len2 = childs.getLength();

                for (int j = 0; j < len2; j++) {
                    Node child = childs.item(j);

                    elements.put(child.getNodeName(), child.getTextContent());
                }
            }

            return elements;

        } catch (TransformerException ex) {
            logger.error("", ex);

            return null;
        }
    }

}