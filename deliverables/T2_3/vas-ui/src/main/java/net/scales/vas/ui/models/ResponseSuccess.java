package net.scales.vas.ui.models;

public class ResponseSuccess {

    private final String status = "success";
    private final Object result;

    public ResponseSuccess(Object result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public Object getResult() {
        return result;
    }

}