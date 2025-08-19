- Setup Steps [Using Maven]:
leave-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   └── lms/
│   │   │   │       ├── LeaveManagementSystem.java
│   │   │   │       ├── model/
│   │   │   │       │   ├── Employee.java
│   │   │   │       │   ├── LeaveRequest.java
│   │   │   │       │   └── LeaveType.java
│   │   │   │       ├── service/
│   │   │   │       │   ├── EmployeeService.java
│   │   │   │       │   └── LeaveService.java
│   │   │   │       ├── repository/
│   │   │   │       │   ├── EmployeeRepository.java
│   │   │   │       │   └── LeaveRepository.java
│   │   │   │       └── exception/
│   │   │   │           └── LeaveManagementException.java
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/
│   ├── pom.xml 
│   └── README.md

Open terminal in VS Code
Run: mvn clean compile
Run: mvn exec:java -Dexec.mainClass="com.lms.LeaveManagementSystem"

- Edge Cases Handled
*Core Edge Cases:
1. Applying for leave before joining date 
2. Applying for more days than available balance 
3. Overlapping leave requests
4. Employee not found 
5. Invalid dates 

*Additional Edge Cases:
1. Weekend Calculation - Only working days counted for leave
2. Future Joining Dates - Prevents adding employees with future joining dates
3. Past Leave Applications - Cannot apply for leave in the past
4. Maximum Consecutive Leave - Limited to 30 days maximum
5. Leave Too Far in Future - Cannot apply more than 1 year in advance
6. Empty/Null Fields - Comprehensive input validation
7. Cancelling Non-Cancellable Leaves - Status-based cancellation rules
8. Pro-rated Leave Balance - Leave balance calculated based on joining date
10. Concurrent Access - Thread-safe repository implementation
11. Leave Balance Restoration - When approved leave is cancelled, balance is restored
12. Email Uniqueness 
13. Invalid Email Format 

- Assumptions Made:
1. Leave Year - Calendar year basis (Jan-Dec)
2. Weekend Policy - Saturdays and Sundays are non-working days
3. Leave Allocation - 24 days annual leave per employee
4. Pro-rating - New employees get pro-rated leave based on joining month
5. Business Rules - HR can approve/reject any leave request
6. Data Persistence - In-memory storage for MVP (can be extended to database)

- Potential Improvements:
1. Database Integration - PostgreSQL/MySQL with JPA/Hibernate
2. REST API - Spring Boot with proper HTTP endpoints
3. Authentication - JWT-based user authentication
4. Role-Based Access - Different permissions for employees vs HR
5. Email Notifications - Automated email alerts for leave status changes
6. Calendar Integration - Google Calendar/Outlook integration
7. Leave Policies - Configurable leave policies per department
8. Reporting - Advanced analytics and reporting features
9. Mobile App - React Native or Flutter mobile application
10. Audit Trail - Complete audit log of all system changes