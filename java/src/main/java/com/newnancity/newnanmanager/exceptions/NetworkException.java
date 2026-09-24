package com.newnancity.newnanmanager.exceptions;

/** Transport failure before an HTTP response was received. */
public final class NetworkException extends NewNanManagerException {
    private static final long serialVersionUID = 1L;

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
