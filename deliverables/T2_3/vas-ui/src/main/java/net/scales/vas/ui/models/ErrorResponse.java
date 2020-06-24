package net.scales.vas.ui.models;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

    private final String status = "error";
    private final int code;
    private final String message;

    public ErrorResponse(HttpStatus status) {
        this.code = status.value();
        this.message = status.getReasonPhrase();
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
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