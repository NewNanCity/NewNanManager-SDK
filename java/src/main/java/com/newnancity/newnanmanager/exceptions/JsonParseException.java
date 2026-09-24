package com.newnancity.newnanmanager.exceptions;

/** Response body could not be deserialized according to the generated contract. */
public final class JsonParseException extends NewNanManagerException {
    private static final long serialVersionUID = 1L;

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
