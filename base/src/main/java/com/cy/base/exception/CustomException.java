package com.cy.base.exception;


public class CustomException extends Exception {

    public static final int ERROR_CODE_NO_NETWORK = 1;
    public static final int ERROR_CODE_SERVER_ERROR = 2;

    private int mErrorCode;

    public int getErrorCode() {
        return mErrorCode;
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(int errorCode, String message) {
        super(message);
        this.mErrorCode = errorCode;
    }

}
