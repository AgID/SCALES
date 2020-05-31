package net.scales.hub.models;

public class ResultResponse {

    private final String status = "success";
    private final Object result;

    public ResultResponse(Object result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public Object getResult() {
        return result;
    }

}