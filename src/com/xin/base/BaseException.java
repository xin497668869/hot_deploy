package com.xin.base;

import org.jetbrains.annotations.NonNls;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class BaseException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BaseException(@NonNls String message) {
        super(message);
    }

    private Exception innerException;

    public BaseException(@NonNls String message, Exception e) {
        super(message);
        this.innerException = e;
    }

    public Exception getInnerException() {
        return innerException;
    }
}
