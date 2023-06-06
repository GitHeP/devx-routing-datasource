package com.lp.sql.routing.exception;

/**
 * @author Peng He
 * @since 1.0
 */
public class InternalRuntimeException extends RuntimeException {

    public InternalRuntimeException(String message) {
        super(message);
    }

    public InternalRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalRuntimeException(Throwable cause) {
        super(cause);
    }

    protected InternalRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
