package com.lp.sql.routing.exception;

/**
 * @author Peng He
 * @since 1.0
 */
public class AnnotationSqlException extends InternalRuntimeException {

    public AnnotationSqlException(String message) {
        super(message);
    }

    public AnnotationSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationSqlException(Throwable cause) {
        super(cause);
    }

    public AnnotationSqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
