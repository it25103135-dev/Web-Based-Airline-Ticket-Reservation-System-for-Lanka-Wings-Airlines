USE master;
GO
IF DB_ID('LankaWingsFeedbackDB') IS NULL
    CREATE DATABASE LankaWingsFeedbackDB;
GO
USE LankaWingsFeedbackDB;
GO

IF OBJECT_ID('dbo.FeedbackReviews','U') IS NOT NULL DROP TABLE dbo.FeedbackReviews;
GO

CREATE TABLE FeedbackReviews (
    ReviewID INT IDENTITY(1,1) PRIMARY KEY,
    PassengerName NVARCHAR(120) NOT NULL,
    Email NVARCHAR(150) NOT NULL,
    BookingReference VARCHAR(30) NOT NULL,
    FlightNo VARCHAR(20) NULL,
    Rating TINYINT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Title NVARCHAR(150) NOT NULL,
    Comment NVARCHAR(1500) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' CHECK (Status IN ('PUBLISHED','HIDDEN')),
    AdminResponse NVARCHAR(1500) NULL,
    CreatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    UpdatedAt DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    RespondedAt DATETIME2 NULL
);
GO
CREATE INDEX IX_FeedbackReviews_EmailBooking ON FeedbackReviews(Email, BookingReference);
CREATE INDEX IX_FeedbackReviews_StatusCreated ON FeedbackReviews(Status, CreatedAt DESC);
GO

INSERT INTO FeedbackReviews(PassengerName,Email,BookingReference,FlightNo,Rating,Title,Comment,Status,AdminResponse,RespondedAt) VALUES
('Demo Passenger','passenger@example.com','LW-REV-1001','LW101',5,'Smooth and friendly journey','Check-in was simple, the cabin crew were helpful, and the flight arrived on time.','PUBLISHED','Thank you for flying with Lanka Wings. We are happy you enjoyed your journey.',SYSDATETIME()),
('Ayesha Fernando','ayesha@example.com','LW-REV-1002','LW205',4,'Good service overall','The staff were professional and the aircraft was clean. Boarding could be a little faster.','PUBLISHED',NULL,NULL),
('Kamal Perera','kamal@example.com','LW-REV-1003','LW330',3,'Comfortable flight','The flight was comfortable but the departure was delayed.','HIDDEN',NULL,NULL);
GO
