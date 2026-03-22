-- Complete Database Schema for MizHoang Project
-- Run this script in SQL Server Management Studio (SSMS)

CREATE DATABASE QLGNBT;
GO

USE QLGNBT;
GO

-- Users table with approval system
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100),
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('student','teacher','admin')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / ACTIVE / REJECTED
    class_id INT NULL
);

-- Classes table for teacher-class management
CREATE TABLE Classes (
    class_id INT IDENTITY(1,1) PRIMARY KEY,
    class_name NVARCHAR(100) NOT NULL,
    teacher_id INT NOT NULL,
    CONSTRAINT FK_Classes_Teacher FOREIGN KEY (teacher_id) REFERENCES Users(user_id)
);

-- Assignments table with file support
CREATE TABLE Assignments (
    assignment_id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    due_date DATETIME,
    created_by INT NOT NULL,
    class_id INT NULL,
    assignment_file NVARCHAR(500) NULL, -- File path for assignment document
    created_date DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Assignments_Teacher FOREIGN KEY (created_by) REFERENCES Users(user_id),
    CONSTRAINT FK_Assignments_Class FOREIGN KEY (class_id) REFERENCES Classes(class_id)
);

-- Submissions table with scoring and deadline extension
CREATE TABLE Submissions (
    submission_id INT IDENTITY(1,1) PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    file_path NVARCHAR(500),
    submit_date DATETIME DEFAULT GETDATE(),
    status NVARCHAR(50) DEFAULT N'Chưa nộp',
    score DECIMAL(3,1) NULL, -- Score from teacher (0-10)
    extended_deadline DATETIME NULL, -- Extension granted by admin
    CONSTRAINT FK_Submissions_Assignment FOREIGN KEY (assignment_id) REFERENCES Assignments(assignment_id),
    CONSTRAINT FK_Submissions_Student FOREIGN KEY (student_id) REFERENCES Users(user_id)
);

-- Sample Data
INSERT INTO Users(username,password,full_name,email,role,status)
VALUES
('admin','admin','Admin','admin@mizhoang.com','admin','ACTIVE'),
('teacher1','123',N'Giáo viên Hoàng','teacher@mizhoang.com','teacher','ACTIVE'),
('student1','123',N'Huy Hoàng','student1@mizhoang.com','student','ACTIVE'),
('student2','123',N'Nguyễn Văn A','student2@mizhoang.com','student','ACTIVE');

INSERT INTO Classes(class_name,teacher_id)
VALUES
(N'Lớp 1A', 2),
(N'Lớp 1B', 2);

INSERT INTO Assignments(title,description,due_date,created_by,class_id,assignment_file)
VALUES
(N'Bài tập 1: Java Servlet cơ bản', N'Viết servlet quản lý bài tập', '2026-04-01 23:59:00', 2, 1, NULL),
(N'Bài tập 2: MVC + JSTL', N'Thực hiện CRUD với JSTL và EL', '2026-04-15 23:59:00', 2, 1, 'uploads/sample_assignment.pdf'),
(N'PRJ301', N'viet code ws2', '2026-03-18 23:59:59', 2, 2, NULL);

INSERT INTO Submissions(assignment_id, student_id, file_path, status, score)
VALUES
(1, 3, 'uploads/1773890677254_EZSEShop.zip', N'Đã nộp', 8.5);

-- End of script