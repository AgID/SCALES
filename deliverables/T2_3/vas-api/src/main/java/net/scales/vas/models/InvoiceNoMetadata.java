package net.scales.vas.models;

public class InvoiceNoMetadata {

    private final String id;
    private final String hubId;
    private final String endEntityId;

    private final String invoiceNumber;

    public InvoiceNoMetadata(
        String id,
        String hubId,
        String endEntityId,
        String invoiceNumber
    ) {
        this.id = id;
        this.hubId = hubId;
        this.endEntityId = endEntityId;

        this.invoiceNumber = invoiceNumber;
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getHubId() {
        return hubId;
    }

    public String getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

}