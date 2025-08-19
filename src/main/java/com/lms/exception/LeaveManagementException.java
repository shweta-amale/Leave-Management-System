package com.lms.exception;

public class LeaveManagementException extends Exception {
    public LeaveManagementException(String message) {
        super(message);
    }
    
    public LeaveManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}