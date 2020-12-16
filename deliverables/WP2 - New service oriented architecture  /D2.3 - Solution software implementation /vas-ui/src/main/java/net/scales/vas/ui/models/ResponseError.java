package net.scales.vas.ui.models;

import org.springframework.http.HttpStatus;

public class ResponseError {

    private final String status = "error";
    private final int code;
    private final String message;

    public ResponseError(HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
    }

    public ResponseError(HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

}