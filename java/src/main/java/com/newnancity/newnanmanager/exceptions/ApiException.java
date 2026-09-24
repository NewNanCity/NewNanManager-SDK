package com.newnancity.newnanmanager.exceptions;

import java.util.List;
import java.util.Map;

/** Structured NewNanManager API error from the standard error response. */
public final class ApiException extends HttpException {
    private static final long serialVersionUID = 1L;

    private final Integer apiCode;
    private final String errorCategory;
    private final String machineCode;

    public ApiException(
            int statusCode,
            String message,
            Integer apiCode,
            String errorCategory,
            String machineCode,
            Map<String, List<String>> responseHeaders,
            String responseBody,
            String requestId,
            String traceId,
            String retryAfter) {
        super(statusCode, message, responseHeaders, responseBody, requestId, traceId, retryAfter);
        this.apiCode = apiCode;
        this.errorCategory = errorCategory;
        this.machineCode = machineCode;
    }

    public Integer getApiCode() {
        return apiCode;
    }

    public String getErrorCategory() {
        return errorCategory;
    }

    public String getMachineCode() {
        return machineCode;
    }
}
