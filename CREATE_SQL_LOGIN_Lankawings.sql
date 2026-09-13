USE master;
GO
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = 'Lankawings')
BEGIN
    CREATE LOGIN [Lankawings] WITH PASSWORD = 'Lankawings123', CHECK_POLICY = OFF;
END;
GO

IF DB_ID('LankaWingsBookingManagementDB') IS NULL
BEGIN
    CREATE DATABASE LankaWingsBookingManagementDB;
END;
GO

USE LankaWingsBookingManagementDB;
GO
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = 'Lankawings')
BEGIN
    CREATE USER [Lankawings] FOR LOGIN [Lankawings];
END;
GO
ALTER ROLE db_owner ADD MEMBER [Lankawings];
GO
