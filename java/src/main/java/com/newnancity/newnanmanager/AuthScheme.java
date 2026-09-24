package com.newnancity.newnanmanager;

/** Authentication scheme used by the NewNanManager HTTP API. */
public enum AuthScheme {
    /** Sends {@code Authorization: Bearer <token>}. */
    BEARER,
    /** Sends the token in the {@code X-API-Token} header. */
    API_TOKEN
}
