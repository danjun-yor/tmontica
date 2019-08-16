package com.internship.tmontica.util.exception;

public class UtilException extends RuntimeException{

    private String field;
    private String message;
    private UtilExceptionType utilExceptionType;

    public UtilException(UtilExceptionType utilExceptionType){
        this.field = utilExceptionType.getField();
        this.message = utilExceptionType.getMessage();
        this.utilExceptionType = utilExceptionType;
    }
}
