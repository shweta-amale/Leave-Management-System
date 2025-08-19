package com.lms;

import com.lms.model.*;
import com.lms.service.*;
import com.lms.exception.LeaveManagementException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class LeaveManagementSystem {
    private final EmployeeService employeeService;
    private final LeaveService leaveService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LeaveManagementSystem() {
        this.employeeService = new EmployeeService();
        this.leaveService = new LeaveService(employeeService);
        this.scanner = new Scanner(System.in);

        // Add some sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        try {
            employeeService.addEmployee("John Doe", "john.doe@company.com", "Engineering", LocalDate.of(2023, 1, 15));
            employeeService.addEmployee("Jane Smith", "jane.smith@company.com", "HR", LocalDate.of(2022, 6, 10));
            employeeService.addEmployee("Mike Johnson", "mike.johnson@company.com", "Marketing",
                    LocalDate.of(2024, 3, 1));
            System.out.println("Sample employees added successfully!");
        } catch (LeaveManagementException e) {
            System.err.println("Error adding sample data: " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("=== Welcome to Leave Management System ===");
        System.out.println("Company: TechCorp | Employees: 50 | MVP Version 1.0\n");

        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> addEmployee();
                    case 2 -> applyForLeave();
                    case 3 -> approveRejectLeave();
                    case 4 -> viewLeaveBalance();
                    case 5 -> viewAllEmployees();
                    case 6 -> viewPendingLeaves();
                    case 7 -> viewLeaveHistory();
                    case 8 -> cancelLeave();
                    case 9 -> viewSystemStatistics();
                    case 0 -> {
                        System.out.println("Thank you for using Leave Management System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("LEAVE MANAGEMENT SYSTEM - MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Add New Employee");
        System.out.println("2. Apply for Leave");
        System.out.println("3. Approve/Reject Leave");
        System.out.println("4. Check Leave Balance");
        System.out.println("5. View All Employees");
        System.out.println("6. View Pending Leaves");
        System.out.println("7. View Leave History");
        System.out.println("8. Cancel Leave Request");
        System.out.println("9. System Statistics");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
    }

    private void addEmployee() {
        System.out.println("\n--- Add New Employee ---");

        try {
            System.out.print("Enter employee name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Enter department: ");
            String department = scanner.nextLine().trim();

            System.out.print("Enter joining date (yyyy-MM-dd): ");
            LocalDate joiningDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);

            Employee employee = employeeService.addEmployee(name, email, department, joiningDate);

            System.out.println("\n✅ Employee added successfully!");
            System.out.println("Employee ID: " + employee.getEmployeeId());
            System.out.println("Leave Balance: " + employee.getAvailableLeaves() + " days");

        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format! Please use yyyy-MM-dd format.");
        } catch (LeaveManagementException e) {
            System.err.println("Failed to add employee: " + e.getMessage());
        }
    }

    private void applyForLeave() {
        System.out.println("\n--- Apply for Leave ---");

        try {
            System.out.print("Enter employee ID: ");
            String employeeId = scanner.nextLine().trim();

            // Show employee details and current balance
            Employee employee = employeeService.getEmployee(employeeId);
            System.out.println("Employee: " + employee.getName() + " | Available Leaves: " +
                    employee.getAvailableLeaves() + " days");

            System.out.print("Enter start date (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);

            System.out.print("Enter end date (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);

            System.out.print("Enter reason: ");
            String reason = scanner.nextLine().trim();

            System.out.println("Select leave type:");
            LeaveType[] types = LeaveType.values();
            for (int i = 0; i < types.length; i++) {
                System.out.println((i + 1) + ". " + types[i].getDisplayName());
            }

            int typeChoice = getIntInput("Enter choice (1-" + types.length + "): ");
            if (typeChoice < 1 || typeChoice > types.length) {
                System.err.println("Invalid leave type selection!");
                return;
            }

            LeaveType leaveType = types[typeChoice - 1];

            LeaveRequest request = leaveService.applyForLeave(employeeId, startDate, endDate, reason, leaveType);

            System.out.println("\n✅ Leave application submitted successfully!");
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Status: " + request.getStatus());
            System.out.println("Days Requested: " + request.getNumberOfDays());

        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format! Please use yyyy-MM-dd format.");
        } catch (LeaveManagementException e) {
            System.err.println("Failed to apply for leave: " + e.getMessage());
        }
    }

    private void approveRejectLeave() {
        System.out.println("\n--- Approve/Reject Leave ---");

        // Show pending leaves first
        List<LeaveRequest> pendingLeaves = leaveService.getPendingLeaves();
        if (pendingLeaves.isEmpty()) {
            System.out.println("No pending leave requests found.");
            return;
        }

        System.out.println("Pending Leave Requests:");
        System.out.println("-".repeat(80));
        for (LeaveRequest request : pendingLeaves) {
            try {
                Employee employee = employeeService.getEmployee(request.getEmployeeId());
                System.out.printf("ID: %s | Employee: %s | Dates: %s to %s | Days: %d | Type: %s%n",
                        request.getRequestId(), employee.getName(),
                        request.getStartDate(), request.getEndDate(),
                        request.getNumberOfDays(), request.getLeaveType());
            } catch (LeaveManagementException e) {
                System.err.println("Error fetching employee details: " + e.getMessage());
            }
        }
        System.out.println("-".repeat(80));

        try {
            System.out.print("Enter request ID to process: ");
            String requestId = scanner.nextLine().trim();

            System.out.print("Approve or Reject? (A/R): ");
            String decision = scanner.nextLine().trim().toUpperCase();

            System.out.print("Enter your name (approver): ");
            String approverName = scanner.nextLine().trim();

            if ("A".equals(decision)) {
                LeaveRequest approved = leaveService.approveLeave(requestId, approverName);
                System.out.println("\n✅ Leave request approved successfully!");
                System.out.println("Request ID: " + approved.getRequestId());
                System.out.println("Approved by: " + approved.getApprovedBy());
                System.out.println("Approved on: " + approved.getApprovedDate());
            } else if ("R".equals(decision)) {
                System.out.print("Enter rejection reason: ");
                String comments = scanner.nextLine().trim();

                LeaveRequest rejected = leaveService.rejectLeave(requestId, approverName, comments);
                System.out.println("\n❌ Leave request rejected successfully!");
                System.out.println("Request ID: " + rejected.getRequestId());
                System.out.println("Rejected by: " + rejected.getApprovedBy());
                System.out.println("Reason: " + rejected.getComments());
            } else {
                System.err.println("Invalid choice! Please enter A for Approve or R for Reject.");
            }

        } catch (LeaveManagementException e) {
            System.err.println("Failed to process leave request: " + e.getMessage());
        }
    }

    private void viewLeaveBalance() {
        System.out.println("\n--- View Leave Balance ---");

        try {
            System.out.print("Enter employee ID: ");
            String employeeId = scanner.nextLine().trim();

            Employee employee = employeeService.getEmployee(employeeId);

            System.out.println("\n📊 Leave Balance Details:");
            System.out.println("-".repeat(40));
            System.out.println("Employee: " + employee.getName());
            System.out.println("Email: " + employee.getEmail());
            System.out.println("Department: " + employee.getDepartment());
            System.out.println("Joining Date: " + employee.getJoiningDate());
            System.out.println("Total Leave Allocation: " + employee.getTotalLeaveBalance() + " days");
            System.out.println("Used Leaves: " + employee.getUsedLeaves() + " days");
            System.out.println("Available Leaves: " + employee.getAvailableLeaves() + " days");
            System.out.println("-".repeat(40));

            // Show recent leave history
            List<LeaveRequest> leaveHistory = leaveService.getLeaveHistory(employeeId);
            if (!leaveHistory.isEmpty()) {
                System.out.println("\nRecent Leave Requests:");
                leaveHistory.stream()
                        .limit(5)
                        .forEach(request -> System.out.println("  • " + request.getStartDate() +
                                " to " + request.getEndDate() + " (" + request.getNumberOfDays() +
                                " days) - " + request.getStatus()));
            }

        } catch (LeaveManagementException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewAllEmployees() {
        System.out.println("\n--- All Employees ---");

        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.printf("%-12s %-20s %-25s %-15s %-12s %-10s%n",
                "ID", "Name", "Email", "Department", "Joining", "Leaves");
        System.out.println("-".repeat(100));

        for (Employee emp : employees) {
            System.out.printf("%-12s %-20s %-25s %-15s %-12s %-10s%n",
                    emp.getEmployeeId(),
                    emp.getName().length() > 19 ? emp.getName().substring(0, 16) + "..." : emp.getName(),
                    emp.getEmail().length() > 24 ? emp.getEmail().substring(0, 21) + "..." : emp.getEmail(),
                    emp.getDepartment(),
                    emp.getJoiningDate(),
                    emp.getAvailableLeaves() + "/" + emp.getTotalLeaveBalance());
        }
    }

    private void viewPendingLeaves() {
        System.out.println("\n--- Pending Leave Requests ---");

        List<LeaveRequest> pendingLeaves = leaveService.getPendingLeaves();
        if (pendingLeaves.isEmpty()) {
            System.out.println("No pending leave requests.");
            return;
        }

        System.out.printf("%-12s %-15s %-12s %-12s %-5s %-15s%n",
                "Request ID", "Employee", "Start Date", "End Date", "Days", "Type");
        System.out.println("-".repeat(80));

        for (LeaveRequest request : pendingLeaves) {
            try {
                Employee employee = employeeService.getEmployee(request.getEmployeeId());
                System.out.printf("%-12s %-15s %-12s %-12s %-5d %-15s%n",
                        request.getRequestId(),
                        employee.getName().length() > 14 ? employee.getName().substring(0, 11) + "..."
                                : employee.getName(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getNumberOfDays(),
                        request.getLeaveType());
            } catch (LeaveManagementException e) {
                System.err.println("Error fetching employee details for request: " + request.getRequestId());
            }
        }
    }

    private void viewLeaveHistory() {
        System.out.println("\n--- Leave History ---");

        try {
            System.out.print("Enter employee ID: ");
            String employeeId = scanner.nextLine().trim();

            Employee employee = employeeService.getEmployee(employeeId);
            List<LeaveRequest> history = leaveService.getLeaveHistory(employeeId);

            System.out.println("\nLeave History for: " + employee.getName());
            System.out.println("-".repeat(60));

            if (history.isEmpty()) {
                System.out.println("No leave requests found.");
                return;
            }

            for (LeaveRequest request : history) {
                System.out.printf("Request ID: %s%n", request.getRequestId());
                System.out.printf("  Dates: %s to %s (%d days)%n",
                        request.getStartDate(), request.getEndDate(), request.getNumberOfDays());
                System.out.printf("  Type: %s | Status: %s%n", request.getLeaveType(), request.getStatus());
                System.out.printf("  Applied: %s%n", request.getAppliedDate());
                if (request.getApprovedBy() != null) {
                    System.out.printf("  Processed by: %s on %s%n",
                            request.getApprovedBy(), request.getApprovedDate());
                }
                if (request.getComments() != null) {
                    System.out.printf("  Comments: %s%n", request.getComments());
                }
                System.out.println("-".repeat(40));
            }

        } catch (LeaveManagementException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void cancelLeave() {
        System.out.println("\n--- Cancel Leave Request ---");

        try {
            System.out.print("Enter request ID to cancel: ");
            String requestId = scanner.nextLine().trim();

            LeaveRequest cancelled = leaveService.cancelLeave(requestId);
            System.out.println("\n✅ Leave request cancelled successfully!");
            System.out.println("Request ID: " + cancelled.getRequestId());
            System.out.println("Status: " + cancelled.getStatus());

        } catch (LeaveManagementException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void viewSystemStatistics() {
        System.out.println("\n--- System Statistics ---");

        List<Employee> allEmployees = employeeService.getAllEmployees();
        List<LeaveRequest> allRequests = leaveService.getAllLeaveRequests();
        List<LeaveRequest> pendingRequests = leaveService.getPendingLeaves();

        long approvedRequests = allRequests.stream()
                .filter(req -> req.getStatus() == LeaveStatus.APPROVED)
                .count();

        long rejectedRequests = allRequests.stream()
                .filter(req -> req.getStatus() == LeaveStatus.REJECTED)
                .count();

        System.out.println("📈 System Overview:");
        System.out.println("-".repeat(30));
        System.out.println("Total Employees: " + allEmployees.size());
        System.out.println("Total Leave Requests: " + allRequests.size());
        System.out.println("Pending Requests: " + pendingRequests.size());
        System.out.println("Approved Requests: " + approvedRequests);
        System.out.println("Rejected Requests: " + rejectedRequests);
        System.out.println("-".repeat(30));
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Please enter a valid number!");
            }
        }
    }

    public static void main(String[] args) {
        new LeaveManagementSystem().run();
    }
}