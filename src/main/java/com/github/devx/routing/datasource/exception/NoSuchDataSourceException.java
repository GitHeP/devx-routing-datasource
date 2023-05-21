package com.github.devx.routing.datasource.exception;

/**
 * @author he peng
 * @since 1.0
 */
public class NoSuchDataSourceException extends RuntimeException {

    public NoSuchDataSourceException() {
    }

    public NoSuchDataSourceException(String message) {
        super(message);
    }

    public NoSuchDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchDataSourceException(Throwable cause) {
        super(cause);
    }

    public NoSuchDataSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
