package net.scales.vas.models;

public class Hub {

    private final String hubId;
    private final String hubVat;
    private final String hubName;

    public Hub(String hubId, String hubVat, String hubName) {
        this.hubId = hubId;
        this.hubVat = hubVat;
        this.hubName = hubName;
    }

    public String getHubId() {
        return hubId;
    }

    public String getHubVat() {
        return hubVat;
    }

    public String getHubName() {
        return hubName;
    }

}