package com.newnancity.newnanmanager.exceptions;

/** Invalid local client configuration or request argument. */
public final class ConfigurationException extends NewNanManagerException {
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
