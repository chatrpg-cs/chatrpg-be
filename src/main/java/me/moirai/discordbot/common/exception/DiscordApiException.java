package me.moirai.discordbot.common.exception;

import org.springframework.http.HttpStatus;

public class DiscordApiException extends RuntimeException {

    private final HttpStatus httpStatusCode;
    private final String errorMessage;
    private final String errorDescription;

    public DiscordApiException(HttpStatus httpStatusCode, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = null;
        this.errorDescription = null;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message, Throwable t) {

        super(message, t);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
