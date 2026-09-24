package com.newnancity.newnanmanager.exceptions;

/** Base class for errors returned by the NewNanManager facade. */
public class NewNanManagerException extends Exception {
    private static final long serialVersionUID = 1L;

    public NewNanManagerException(String message) {
        super(message);
    }

    public NewNanManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
