IF DB_ID('LankaWingsDB') IS NULL
BEGIN
    CREATE DATABASE LankaWingsDB;
END;
GO
USE LankaWingsDB;
GO

IF OBJECT_ID('dbo.PaymentAuditFlags','U') IS NOT NULL DROP TABLE dbo.PaymentAuditFlags;
IF OBJECT_ID('dbo.Payments','U') IS NOT NULL DROP TABLE dbo.Payments;
IF OBJECT_ID('dbo.PaymentOrders','U') IS NOT NULL DROP TABLE dbo.PaymentOrders;
GO

CREATE TABLE PaymentOrders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    BookingReference VARCHAR(20) NOT NULL UNIQUE,
    PassengerName VARCHAR(120) NOT NULL,
    Email VARCHAR(160) NULL,
    RouteSummary VARCHAR(180) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL CHECK (Amount >= 0),
    OrderStatus VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (OrderStatus IN ('PENDING','PAID','CANCELLED','REFUNDED')),
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

CREATE TABLE Payments (
    PaymentID INT IDENTITY(1,1) PRIMARY KEY,
    OrderID INT NOT NULL,
    Amount DECIMAL(10,2) NOT NULL CHECK (Amount >= 0),
    Method VARCHAR(30) NOT NULL,
    CardLast4 CHAR(4) NULL,
    TransactionRef VARCHAR(40) NOT NULL UNIQUE,
    GatewayStatus VARCHAR(20) NOT NULL
        CHECK (GatewayStatus IN ('SUCCESS','FAILED','REFUNDED')),
    FailureReason VARCHAR(300) NULL,
    PaidAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_Payments_Orders FOREIGN KEY (OrderID) REFERENCES PaymentOrders(OrderID)
);
GO

CREATE TABLE PaymentAuditFlags (
    FlagID INT IDENTITY(1,1) PRIMARY KEY,
    PaymentID INT NOT NULL,
    Issue VARCHAR(500) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (Status IN ('OPEN','RESOLVED')),
    FlaggedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    ResolvedAt DATETIME2 NULL,
    CONSTRAINT FK_AuditFlags_Payments FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID)
);
GO

CREATE INDEX IX_Payments_OrderID ON Payments(OrderID);
CREATE INDEX IX_Payments_PaidAt ON Payments(PaidAt DESC);
CREATE INDEX IX_AuditFlags_Status ON PaymentAuditFlags(Status);
GO

-- Sample unpaid orders for testing this standalone payment module.
INSERT INTO PaymentOrders(BookingReference,PassengerName,Email,RouteSummary,Amount)
VALUES
('LW-PAY-1001','A. Perera','aperera@example.com','Colombo (CMB) → Dubai (DXB)',68500.00),
('LW-PAY-1002','M. Silva','msilva@example.com','Colombo (CMB) → Singapore (SIN)',74250.00),
('LW-PAY-1003','N. Fernando','nfernando@example.com','Colombo (CMB) → Kuala Lumpur (KUL)',59800.00);
GO
