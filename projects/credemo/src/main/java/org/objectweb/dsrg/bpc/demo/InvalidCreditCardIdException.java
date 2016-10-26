package org.objectweb.dsrg.bpc.demo;

public class InvalidCreditCardIdException extends Exception {

    public InvalidCreditCardIdException() {
        super();
    }

    public InvalidCreditCardIdException(String message) {
        super(message);
    }

    public InvalidCreditCardIdException(Throwable cause) {
        super(cause);
    }

    public InvalidCreditCardIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
