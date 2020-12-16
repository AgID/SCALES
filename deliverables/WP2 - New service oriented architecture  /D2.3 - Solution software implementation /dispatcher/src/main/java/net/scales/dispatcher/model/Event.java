package net.scales.dispatcher.model;

public class Event {

    private final String type;
    private final String data;
    private final String hubId;
    private final String endEntityId;
    private final String vasUrl;

    public Event(
        String type,
        String data,
        String hubId,
        String endEntityId,
        String vasUrl
    ) {
        this.type = type;
        this.data = data;
        this.hubId = hubId;
        this.endEntityId = endEntityId;
        this.vasUrl = vasUrl;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getHubId() {
        return hubId;
    }

    public String getEndEntityId() {
        return endEntityId;
    }

    public String getVasUrl() {
        return vasUrl;
    }

}