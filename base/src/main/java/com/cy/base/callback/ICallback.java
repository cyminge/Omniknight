package com.cy.base.callback;

import com.cy.base.exception.ResponseException;

import java.io.Serializable;

/**
 * Created by cy on 19-4-3.
 */

public interface ICallback<T> extends Serializable {

    void onSuccess(final T result);

    void onError(final ResponseException error);
}
