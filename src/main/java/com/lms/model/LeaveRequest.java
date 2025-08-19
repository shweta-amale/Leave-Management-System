package com.lms.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LeaveRequest {
    private String requestId;
    private String employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveType leaveType;
    private LeaveStatus status;
    private LocalDate appliedDate;
    private String approvedBy;
    private LocalDate approvedDate;
    private String comments;

    // Constructors
    public LeaveRequest() {
    }

    public LeaveRequest(String requestId, String employeeId, LocalDate startDate,
            LocalDate endDate, String reason, LeaveType leaveType) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.leaveType = leaveType;
        this.status = LeaveStatus.PENDING;
        this.appliedDate = LocalDate.now();
    }

    public long getNumberOfDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    // Getters and Setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return String.format(
                "LeaveRequest{id='%s', employeeId='%s', startDate=%s, endDate=%s, days=%d, type=%s, status=%s}",
                requestId, employeeId, startDate, endDate, getNumberOfDays(), leaveType, status);
    }
}