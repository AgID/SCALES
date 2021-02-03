package net.scales.vas.models;

public class ResponseListSuccess {

    private final String status = "success";
    private final long elementCounter;
    private final Object result;

    public ResponseListSuccess(long elementCounter, Object result) {
        this.elementCounter = elementCounter;
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public Object getResult() {
        return result;
    }

    public Object getElementCounter() {
        return elementCounter;
    }

}