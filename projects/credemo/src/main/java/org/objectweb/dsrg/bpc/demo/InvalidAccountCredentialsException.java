package org.objectweb.dsrg.bpc.demo;

public class InvalidAccountCredentialsException extends Exception {

    public InvalidAccountCredentialsException() {
        super();
    }

    public InvalidAccountCredentialsException(String message) {
        super(message);
    }

    public InvalidAccountCredentialsException(Throwable cause) {
        super(cause);
    }

    public InvalidAccountCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

}
