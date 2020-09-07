package net.scales.vas.models;

public class EndEntity {

    private final String endEntityId;
    private final String endEntityAccount;
    private final String endEntityName;
    private final String endEntityDescription;
    private final String endEndityFiscalCode;

    public EndEntity(
        String endEntityId,
        String endEntityAccount,
        String endEntityName,
        String endEntityDescription,
        String endEndityFiscalCode
    ) {
        this.endEntityId = endEntityId;
        this.endEntityAccount = endEntityAccount;
        this.endEntityName = endEntityName;
        this.endEntityDescription = endEntityDescription;
        this.endEndityFiscalCode = endEndityFiscalCode;
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getEndEntityAccount() {
        return endEntityAccount;
    }

    public String getEndEntityName() {
        return endEntityName;
    }

    public String getEndEntityDescription() {
        return endEntityDescription;
    }

    public String getEndEntityFiscalCode() {
        return endEndityFiscalCode;
    }

}