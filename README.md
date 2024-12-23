# Credit Module API Documentation

## Overview
The Credit Module is a Spring Boot-based application that manages loan operations for a banking system. It provides REST APIs for creating loans, managing installments, and handling payments with role-based access control.

## Project Architecture Overview
In this project, I strive to follow Domain-Driven Design (DDD) principles and Hexagonal Architecture patterns. My main focus is on writing clean, maintainable code with well-defined bounded contexts. I aim to isolate core business logic from external concerns through clear architectural boundaries.

### Key Components

- **Application Layer**: Contains application services and command objects that orchestrate the business logic
- **Domain Layer**: Core business logic and domain entities
- **Infrastructure Layer**: External concerns including:
  - Inbound adapters (REST API) for handling incoming requests
  - Outbound adapters (Persistence) for external dependencies
  - Security and configuration components

## Features
- Create and manage loans
- Track loan installments
- Process loan payments
- Role-based access control (ADMIN and CUSTOMER roles)
- Secure API endpoints
- Validation for all operations

## Security
The application implements role-based security with two main roles:
- **ADMIN**: Can access all loans and operations
- **CUSTOMER**: Can only access their own loans and related operations

## Authentication

All endpoints require Basic Authentication. Use test username and password in the Authorization header.

### Available Test Users

1. **Admin User**
```http
Authorization: Basic admin admin    # Role: ADMIN - Full access to all operations
```

2. **Test Customer Users**
```http
Authorization: Basic customer customer     # Role: CUSTOMER - Customer ID: 1, Credit Limit: 10000
Authorization: Basic customer2 customer2   # Role: CUSTOMER - Customer ID: 2, Credit Limit: 10000
```

## API Endpoints

### Loan Operations

#### 1. Create Loan
```http
POST /api/v1/loan
Content-Type: application/json
Authorization: Basic customer customer

{
    "customerId": 1,
    "amount": "10000.00",
    "numberOfInstallments": 12,
    "interestRate": "0.15"
}
```

#### 2. Search Loans
```http
GET /api/v1/loan/search
Content-Type: application/json
Authorization: Basic customer customer

{
    "customerId": 1,
    "numberOfInstallments": 12,  // optional
    "isPaid": false             // optional
}
```

#### 3. Pay Loan Installments
```http
POST /api/v1/loan/pay
Content-Type: application/json
Authorization: Basic customer customer

{
    "customerId": 1,
    "loanId": 1,
    "amount": "1000.00"
}
```
### Loan Installment Operations

#### 1. Get Installments
```http
GET /api/v1/installment/search
Content-Type: application/json
Authorization: Basic customer customer

{
    "customerId": 1,
    "loanId": 1
}
```

## Business Rules

### Loan Creation
- Customer must have sufficient credit limit
- Interest rate must be between``` 0.1``` and ```0.5 ```
- Number of installments must be one of:``` 6, 9, 12, 24```
- Due Date of Installments should be first day of months
- Total loan amount is calculated as ``` amount * (1 + interestRate).```
  

### Loan Payment Rules

#### Full Installment Payment
- Installments must be paid fully or not at all
- Example:  if installments amount is 10
  - Payment of 20: Pays 2 installments
  - Payment of 15: Pays 1 installment, 5 is returned
  - Payment of 5: No payment processed

#### Chronological Payment
- Payments are applied to the earliest unpaid installment first
- Installments must be due within the next 3 months to be paid
- Example: In January
    - Can pay: January, February, March installments
    - Cannot pay: April onwards

#### Reward & Penalty System
- Early Payment Discount:
  - Formula: `discount = installmentAmount * 0.001 * (days before due date)`
- Late Payment Penalty:
  - Formula: `penalty = installmentAmount * 0.001 * (days after due date)`

## Technical Details

### Requirements
- Java 21
- Maven 3.8.8

### Dependencies
- Spring Boot 3.4.1
  - Spring Web
  - Spring Data JPA
  - Spring Security (Basic Auth)
  - Spring Validation
- H2 Database (in-memory)
- Lombok

### Building and Running
1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start with an in-memory H2 database and create test users automatically.