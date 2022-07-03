package org.minigame.configuration;

public class MiniGameException extends RuntimeException{

    private HttpStatus httpStatus;

    public MiniGameException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
