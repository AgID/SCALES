package net.scales.vas.models;

public class EndEntity {

    private final String endEntityId;
    private final String endEntityVat;
    private final String endEntityName;

    public EndEntity(String endEntityId, String endEntityVat, String endEntityName) {
        this.endEntityId = endEntityId;
        this.endEntityVat = endEntityVat;
        this.endEntityName = endEntityName;
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getEndEntityVat() {
        return endEntityVat;
    }

    public String getEndEntityName() {
        return endEntityName;
    }

}