package com.newnancity.newnanmanager.exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

/** Converts generated OpenAPI exceptions into the stable facade exception model. */
public final class ApiExceptionMapper {
    private ApiExceptionMapper() {
    }

    /** Map a successful HTTP response whose body cannot be decoded. */
    public static JsonParseException mapJsonParseFailure(
            com.google.gson.JsonParseException exception) {
        return new JsonParseException("response body could not be parsed", exception);
    }

    public static NewNanManagerException map(com.newnancity.newnanmanager.generated.ApiException exception) {
        int statusCode = exception.getCode();
        Map<String, List<String>> headers = exception.getResponseHeaders();
        String responseBody = exception.getResponseBody();
        Throwable cause = exception.getCause();

        if (statusCode == 0) {
            if (cause != null) {
                return new NetworkException("request failed before receiving an HTTP response", cause);
            }
            return new ConfigurationException("generated client rejected the request");
        }

        if (responseBody == null && cause != null) {
            return new JsonParseException("response body could not be parsed", cause);
        }

        ParsedError parsed = parse(responseBody);
        String message = firstNonBlank(parsed.message, parsed.detail, "HTTP " + statusCode);
        String requestId = firstNonBlank(header(headers, "X-Request-ID"), parsed.requestId, null);
        String traceId = firstNonBlank(header(headers, "X-Trace-ID"), parsed.traceId, null);
        String retryAfter = header(headers, "Retry-After");

        if (parsed.hasStructuredError()) {
            return new ApiException(
                    statusCode,
                    message,
                    parsed.apiCode,
                    parsed.category,
                    parsed.machineCode,
                    headers,
                    responseBody,
                    requestId,
                    traceId,
                    retryAfter);
        }
        return new HttpException(
                statusCode,
                message,
                headers,
                responseBody,
                requestId,
                traceId,
                retryAfter);
    }

    private static ParsedError parse(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new ParsedError();
        }
        try {
            JsonElement root = JsonParser.parseString(responseBody);
            if (!root.isJsonObject()) {
                return new ParsedError();
            }
            JsonObject object = root.getAsJsonObject();
            ParsedError parsed = new ParsedError();
            parsed.message = stringValue(object, "message");
            parsed.detail = stringValue(object, "detail");
            parsed.requestId = stringValue(object, "request_id");
            parsed.traceId = stringValue(object, "trace_id");
            parsed.apiCode = integerValue(object, "code");
            JsonElement errorElement = object.get("error");
            if (errorElement != null && errorElement.isJsonObject()) {
                JsonObject error = errorElement.getAsJsonObject();
                parsed.category = stringValue(error, "category");
                parsed.machineCode = stringValue(error, "code");
            }
            return parsed;
        } catch (RuntimeException ignored) {
            return new ParsedError();
        }
    }

    private static String stringValue(JsonObject object, String name) {
        JsonElement value = object.get(name);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }

    private static Integer integerValue(JsonObject object, String name) {
        JsonElement value = object.get(name);
        if (value == null || value.isJsonNull()) {
            return null;
        }
        try {
            return value.getAsInt();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static String header(Map<String, List<String>> headers, String name) {
        if (headers == null) {
            return null;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (!name.equalsIgnoreCase(entry.getKey()) || entry.getValue() == null
                    || entry.getValue().isEmpty()) {
                continue;
            }
            return entry.getValue().get(0);
        }
        return null;
    }

    private static String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.trim().isEmpty()) {
            return first;
        }
        if (second != null && !second.trim().isEmpty()) {
            return second;
        }
        return fallback;
    }

    private static final class ParsedError {
        private Integer apiCode;
        private String message;
        private String detail;
        private String requestId;
        private String traceId;
        private String category;
        private String machineCode;

        private boolean hasStructuredError() {
            return apiCode != null || category != null || machineCode != null || message != null;
        }
    }
}
