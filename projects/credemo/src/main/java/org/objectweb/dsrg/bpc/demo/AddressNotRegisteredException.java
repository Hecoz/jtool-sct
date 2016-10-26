package org.objectweb.dsrg.bpc.demo;

public class AddressNotRegisteredException extends Exception {

    public AddressNotRegisteredException() {
        super();
    }

    public AddressNotRegisteredException(String message) {
        super(message);
    }

    public AddressNotRegisteredException(Throwable cause) {
        super(cause);
    }

    public AddressNotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

}
