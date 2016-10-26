package org.objectweb.dsrg.bpc.demo;

public class InvalidFlyTicketIdException extends Exception {

    public InvalidFlyTicketIdException() {
        super();
    }

    public InvalidFlyTicketIdException(String message) {
        super(message);
    }

    public InvalidFlyTicketIdException(Throwable cause) {
        super(cause);
    }

    public InvalidFlyTicketIdException(String message, Throwable cause) {
        super(message, cause);
    }

}
