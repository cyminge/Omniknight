package com.cy.base.exception;

import java.io.IOException;


public class ServerException extends IOException {

    public int getErrorCode() {
        return mErrorCode;
    }

    private int mErrorCode;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(int errorCode, String message) {
        super(message);
        this.mErrorCode = errorCode;
    }

}
