package net.scales.flows;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.scales.flows.services.MetadataExtractingService;

@StartableByRPC
public class ExtractInvoiceMetadataFlow extends FlowLogic<LinkedHashMap<String, String>> {

    private final byte[] invoice;

    public ExtractInvoiceMetadataFlow(byte[] invoice) {
        this.invoice = invoice;
    }

    @Override
    @Suspendable
    public LinkedHashMap<String, String> call() throws FlowException {
        try {
            MetadataExtractingService extractor = getServiceHub().cordaService(MetadataExtractingService.class);

            return new LinkedHashMap<String, String>(extractor.getInvoiceMetadata(new ByteArrayInputStream(invoice)));

        } catch (Exception ex) {
            throw new FlowException(ex.getMessage());
        }
    }

}