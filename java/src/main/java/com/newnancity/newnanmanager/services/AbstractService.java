package com.newnancity.newnanmanager.services;

import com.newnancity.newnanmanager.exceptions.ApiExceptionMapper;
import com.newnancity.newnanmanager.exceptions.NewNanManagerException;

/** Common exception boundary for service wrappers. */
public abstract class AbstractService {
    @FunctionalInterface
    protected interface GeneratedRequest<T> {
        T execute() throws com.newnancity.newnanmanager.generated.ApiException;
    }

    protected final <T> T execute(GeneratedRequest<T> request) throws NewNanManagerException {
        try {
            return request.execute();
        } catch (com.newnancity.newnanmanager.generated.ApiException exception) {
            throw ApiExceptionMapper.map(exception);
        } catch (com.google.gson.JsonParseException exception) {
            throw ApiExceptionMapper.mapJsonParseFailure(exception);
        }
    }

    protected final void executeVoid(GeneratedRequest<?> request) throws NewNanManagerException {
        execute(request);
    }
}
