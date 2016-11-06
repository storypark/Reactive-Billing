package com.github.lukaspili.reactivebilling;

public final class BillingRequestFailedException extends RuntimeException {

    /*package*/ BillingRequestFailedException() {
        super();
    }

    /*package*/ BillingRequestFailedException(String message) {
        super(message);
    }

    /*package*/ BillingRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
