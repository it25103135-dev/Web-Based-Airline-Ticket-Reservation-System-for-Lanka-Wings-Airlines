IF DB_ID('LankaWingsBookingManagementDB') IS NULL
BEGIN
    CREATE DATABASE LankaWingsBookingManagementDB;
END;
GO
USE LankaWingsBookingManagementDB;
GO

IF OBJECT_ID('dbo.Bookings','U') IS NOT NULL DROP TABLE dbo.Bookings;
IF OBJECT_ID('dbo.Flights','U') IS NOT NULL DROP TABLE dbo.Flights;
GO

-- Flight data is read-only reference data for booking details and e-tickets.
-- No Flight Management functions are included in this module.
CREATE TABLE Flights (
    FlightID INT IDENTITY(1,1) PRIMARY KEY,
    FlightNo VARCHAR(15) NOT NULL UNIQUE,
    Origin VARCHAR(80) NOT NULL,
    Destination VARCHAR(80) NOT NULL,
    DepartureTime DATETIME2 NOT NULL,
    ArrivalTime DATETIME2 NOT NULL,
    Fare DECIMAL(10,2) NOT NULL CHECK (Fare >= 0)
);

CREATE TABLE Bookings (
    BookingID INT IDENTITY(1,1) PRIMARY KEY,
    PNR VARCHAR(20) NOT NULL UNIQUE,
    FlightID INT NOT NULL,
    PassengerName VARCHAR(120) NOT NULL,
    PassportNo VARCHAR(40) NOT NULL,
    SeatNumber VARCHAR(8) NOT NULL,
    BookingStatus VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
        CHECK (BookingStatus IN ('CONFIRMED','CANCELLED')),
    BookedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_Bookings_Flights FOREIGN KEY (FlightID) REFERENCES Flights(FlightID)
);
GO

-- Prevent two active bookings from using the same seat on the same flight.
CREATE UNIQUE INDEX UX_Bookings_ActiveSeat
ON Bookings(FlightID, SeatNumber)
WHERE BookingStatus <> 'CANCELLED';
GO

INSERT INTO Flights (FlightNo,Origin,Destination,DepartureTime,ArrivalTime,Fare) VALUES
('LW101','Colombo (CMB)','Dubai (DXB)',DATEADD(DAY,3,SYSDATETIME()),DATEADD(HOUR,8,DATEADD(DAY,3,SYSDATETIME())),78500.00),
('LW204','Colombo (CMB)','Doha (DOH)',DATEADD(DAY,5,SYSDATETIME()),DATEADD(HOUR,7,DATEADD(DAY,5,SYSDATETIME())),69900.00),
('LW330','Colombo (CMB)','Singapore (SIN)',DATEADD(DAY,8,SYSDATETIME()),DATEADD(HOUR,5,DATEADD(DAY,8,SYSDATETIME())),92500.00);
GO

-- Sample booking records are included so Passenger and Admin booking-management pages can be tested immediately.
INSERT INTO Bookings (PNR,FlightID,PassengerName,PassportNo,SeatNumber,BookingStatus,BookedAt) VALUES
('LWBM1001',1,'Nimal Perera','N1234567','4A','CONFIRMED',DATEADD(DAY,-12,SYSDATETIME())),
('LWBM1002',2,'Nimal Perera','N1234567','8C','CONFIRMED',DATEADD(DAY,-5,SYSDATETIME())),
('LWBM1003',3,'Ayesha Silva','P7654321','3F','CONFIRMED',DATEADD(DAY,-2,SYSDATETIME()));
GO
