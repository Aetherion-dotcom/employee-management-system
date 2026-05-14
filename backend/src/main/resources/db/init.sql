-- ============================================
-- WorkForceHub - Database Schema
-- MySQL 8.0+
-- ============================================

CREATE DATABASE IF NOT EXISTS workforcehub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE workforcehub;

-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
) ENGINE=InnoDB;

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    failed_login_attempts INT DEFAULT 0,
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_password_token VARCHAR(255),
    refresh_token VARCHAR(512),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_user_email (email),
    INDEX idx_user_username (username)
) ENGINE=InnoDB;

-- User Roles (Join Table)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Departments
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    head_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_dept_name (name),
    INDEX idx_dept_code (code)
) ENGINE=InnoDB;

-- Employees
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    gender ENUM('MALE','FEMALE','OTHER','PREFER_NOT_TO_SAY'),
    date_of_birth DATE,
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    designation VARCHAR(100),
    joining_date DATE NOT NULL,
    termination_date DATE,
    status ENUM('ACTIVE','INACTIVE','ON_LEAVE','TERMINATED','PROBATION') DEFAULT 'ACTIVE',
    salary DECIMAL(12,2),
    profile_image VARCHAR(255),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    bank_account_number VARCHAR(30),
    bank_name VARCHAR(100),
    tax_id VARCHAR(30),
    shift_start VARCHAR(10),
    shift_end VARCHAR(10),
    user_id BIGINT,
    department_id BIGINT,
    manager_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_emp_employee_id (employee_id),
    INDEX idx_emp_email (email),
    INDEX idx_emp_department (department_id),
    INDEX idx_emp_status (status),
    INDEX idx_emp_joining_date (joining_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (manager_id) REFERENCES employees(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Add foreign key for department head after employees table exists
ALTER TABLE departments ADD CONSTRAINT fk_dept_head FOREIGN KEY (head_id) REFERENCES employees(id) ON DELETE SET NULL;

-- Attendance
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    status ENUM('PRESENT','ABSENT','LATE','HALF_DAY','ON_LEAVE','HOLIDAY','WORK_FROM_HOME') DEFAULT 'ABSENT',
    work_hours DOUBLE,
    overtime_hours DOUBLE,
    is_late BOOLEAN DEFAULT FALSE,
    late_minutes INT,
    notes VARCHAR(500),
    ip_address VARCHAR(50),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    UNIQUE KEY uk_employee_date (employee_id, attendance_date),
    INDEX idx_att_employee (employee_id),
    INDEX idx_att_date (attendance_date),
    INDEX idx_att_status (status),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Leave Requests
CREATE TABLE IF NOT EXISTS leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    leave_type ENUM('CASUAL','SICK','PAID','UNPAID','MATERNITY','PATERNITY','BEREAVEMENT','COMPENSATORY') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
    approved_by BIGINT,
    rejection_reason VARCHAR(500),
    total_days BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_lr_employee (employee_id),
    INDEX idx_lr_status (status),
    INDEX idx_lr_dates (start_date, end_date),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Payroll
CREATE TABLE IF NOT EXISTS payroll (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    allowances DECIMAL(12,2) DEFAULT 0,
    deductions DECIMAL(12,2) DEFAULT 0,
    tax DECIMAL(12,2) DEFAULT 0,
    overtime_pay DECIMAL(12,2) DEFAULT 0,
    bonus DECIMAL(12,2) DEFAULT 0,
    net_salary DECIMAL(12,2),
    is_paid BOOLEAN DEFAULT FALSE,
    paid_date DATE,
    payment_method VARCHAR(50),
    transaction_reference VARCHAR(100),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_pay_employee (employee_id),
    INDEX idx_pay_period (pay_period_start, pay_period_end),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Documents
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT,
    file_path VARCHAR(255) NOT NULL,
    document_type VARCHAR(50),
    description VARCHAR(500),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_doc_employee (employee_id),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    details VARCHAR(2000),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    old_value TEXT,
    new_value TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_audit_user (username),
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_type, entity_id)
) ENGINE=InnoDB;

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    link VARCHAR(500),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_read (is_read),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;
