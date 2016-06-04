/******************************************************************************
 * Copyright 2016 Fabian Lupa                                                 *
 ******************************************************************************/

package com.flaiker.zero.injection;

/**
 * Exception describing an error in a dependency injection process
 */
public class InjectionFailedException extends RuntimeException {
    public InjectionFailedException() {
        super("Dependency injection failed");
    }

    public InjectionFailedException(String message) {
        super(message);
    }

    public InjectionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionFailedException(Throwable cause) {
        super(cause);
    }

    public InjectionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
