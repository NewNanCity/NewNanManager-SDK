package com.newnancity.newnanmanager.exceptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** HTTP response failure with response metadata preserved for caller decisions. */
public class HttpException extends NewNanManagerException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String errorMessage;
    private final Map<String, List<String>> responseHeaders;
    private final String responseBody;
    private final String requestId;
    private final String traceId;
    private final String retryAfter;

    public HttpException(
            int statusCode,
            String message,
            Map<String, List<String>> responseHeaders,
            String responseBody,
            String requestId,
            String traceId,
            String retryAfter) {
        super("HTTP " + statusCode + ": " + message);
        this.statusCode = statusCode;
        this.errorMessage = message;
        this.responseHeaders = responseHeaders == null
                ? Collections.<String, List<String>>emptyMap()
                : responseHeaders;
        this.responseBody = responseBody;
        this.requestId = requestId;
        this.traceId = traceId;
        this.retryAfter = retryAfter;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /** Safe server-provided message without transport decoration. */
    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getRetryAfter() {
        return retryAfter;
    }
}
