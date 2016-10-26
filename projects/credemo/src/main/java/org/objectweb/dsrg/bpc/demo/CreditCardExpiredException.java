package org.objectweb.dsrg.bpc.demo;

public class CreditCardExpiredException extends Exception {

    public CreditCardExpiredException() {
        super();
    }

    public CreditCardExpiredException(String message) {
        super(message);
    }

    public CreditCardExpiredException(Throwable cause) {
        super(cause);
    }

    public CreditCardExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}
