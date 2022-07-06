package org.minigame.configuration;

public class MiniGameResponse {

    private final HttpStatus httpStatus;
    private final String message;

    public MiniGameResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
