package com.github.devx.routing.datasource.exception;

/**
 * @author he peng
 * @since 1.0
 */
public class UnsupportedSqlException extends RuntimeException {

    public UnsupportedSqlException() {
    }

    public UnsupportedSqlException(String message) {
        super(message);
    }

    public UnsupportedSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedSqlException(Throwable cause) {
        super(cause);
    }

    public UnsupportedSqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
