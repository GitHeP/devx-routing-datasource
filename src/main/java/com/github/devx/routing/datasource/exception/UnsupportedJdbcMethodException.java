package com.github.devx.routing.datasource.exception;

/**
 * @author he peng
 * @since 1.0
 */
public class UnsupportedJdbcMethodException extends RuntimeException {

    public UnsupportedJdbcMethodException() {
    }

    public UnsupportedJdbcMethodException(String message) {
        super(message);
    }

    public UnsupportedJdbcMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedJdbcMethodException(Throwable cause) {
        super(cause);
    }

    public UnsupportedJdbcMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
