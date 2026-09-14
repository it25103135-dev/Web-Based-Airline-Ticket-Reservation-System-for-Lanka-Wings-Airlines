IF DB_ID('LankaWingsDB') IS NULL
BEGIN
    CREATE DATABASE LankaWingsDB;
END;
GO

USE LankaWingsDB;
GO

IF OBJECT_ID('dbo.Notifications', 'U') IS NOT NULL DROP TABLE dbo.Notifications;
IF OBJECT_ID('dbo.Users', 'U') IS NOT NULL DROP TABLE dbo.Users;
GO

CREATE TABLE Users (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    FullName VARCHAR(120) NOT NULL,
    Username VARCHAR(60) NOT NULL UNIQUE,
    Email VARCHAR(120) NOT NULL UNIQUE,
    Phone VARCHAR(25),
    PasswordHash CHAR(64) NOT NULL,
    Role VARCHAR(20) NOT NULL DEFAULT 'PASSENGER' CHECK (Role IN ('PASSENGER','ADMIN','FINANCE')),
    Status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (Status IN ('ACTIVE','INACTIVE')),
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);

CREATE TABLE Notifications (
    NotificationID INT IDENTITY(1,1) PRIMARY KEY,
    UserID INT NOT NULL,
    Title VARCHAR(150) NOT NULL,
    Message VARCHAR(800) NOT NULL,
    Type VARCHAR(30) NOT NULL DEFAULT 'INFO',
    IsRead BIT NOT NULL DEFAULT 0,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_Notifications_Users FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

INSERT INTO Users (FullName, Username, Email, Phone, PasswordHash, Role)
VALUES
('Nizla - System Administrator', 'admin@lankawings.com', 'admin@lankawings.lk', '+94 77 000 0000', 'a60c8dc5debc1c13ac5c61a6387ec2b0841c2fd8cc3df3b1873b476c58cc77fd', 'ADMIN'),
('Demo Passenger', 'passenger', 'passenger@example.com', '+94 71 111 2233', 'b794de402884935e0b63f620355f45ee0ef95bbfc4badd57cc16efd3086e30f1', 'PASSENGER');

INSERT INTO Notifications (UserID, Title, Message, Type)
SELECT UserID, 'Welcome to Lanka Wings', 'Your passenger account is ready.', 'WELCOME'
FROM Users WHERE Username = 'passenger';
GO
