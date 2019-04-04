package com.cy.base.exception;

import java.io.IOException;


public class ResponseException extends IOException {

    public int getErrorCode() {
        return mErrorCode;
    }

    private int mErrorCode;

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, int errorCode) {
        super(message);
        this.mErrorCode = errorCode;
    }



}
