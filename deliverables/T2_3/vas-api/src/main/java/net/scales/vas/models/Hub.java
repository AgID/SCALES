package net.scales.vas.models;

public class Hub {

    private final String hubId;
    private final String hubAccount;

    public Hub(String hubId, String hubAccount) {
        this.hubId = hubId;
        this.hubAccount = hubAccount;
    }

    public String getHubId() {
        return hubId;
    }

    public String getHubAccount() {
        return hubAccount;
    }

}