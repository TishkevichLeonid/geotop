package com.geotop.geotopproject.exception;

public class ErrorResponse {
    private String message;

    //TODO: refactor to enum with error codes
    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
