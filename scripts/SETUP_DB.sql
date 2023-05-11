IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'demo')
	CREATE DATABASE demo
GO

USE demo;
GO

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Employees' and xtype='U')
	CREATE TABLE Employees (
		id bigint IDENTITY(1,1) PRIMARY KEY,
		first_name varchar(255) NOT NULL,
		last_name varchar(255),
		age int NOT NULL,
		eligibility bit NOT NULL,
	)
GO

-- Checks if login demoUser already exists for this mssql instance
IF NOT EXISTS (SELECT name FROM master.sys.server_principals WHERE name='demoUser')
	CREATE LOGIN demoUser WITH PASSWORD = 'demo_pass';
GO

-- Checks if user demoUser already exists for demo database
IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name='demoUser1')
	CREATE USER demoUser1 FOR LOGIN demoUser;
GO

-- Grant all READ and WRITE permissions to demoUser1
EXEC sp_addrolemember N'db_datareader', N'demoUser1';
EXEC sp_addrolemember N'db_datawriter', N'demoUser1';
GO
