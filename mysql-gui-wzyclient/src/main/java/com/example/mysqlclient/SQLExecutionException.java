package com.example.mysqlclient;

public class SQLExecutionException extends Exception {
    public SQLExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
     public SQLExecutionException(String message) {
        super(message);
    }
}
