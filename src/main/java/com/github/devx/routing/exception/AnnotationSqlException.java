package com.github.devx.routing.exception;

/**
 * @author Peng He
 * @since 1.0
 */
public class AnnotationSqlException extends RuntimeException {

    public AnnotationSqlException() {
    }

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
