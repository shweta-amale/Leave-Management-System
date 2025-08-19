package com.lms.model;

public enum LeaveType {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    EMERGENCY("Emergency Leave"),
    CASUAL("Casual Leave");
    
    private final String displayName;
    
    LeaveType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}