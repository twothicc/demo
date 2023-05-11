USE demo;
CREATE TABLE Employees (
	ID bigint IDENTITY(1,1) PRIMARY KEY,
	FirstName varchar(255) NOT NULL,
	LastName varchar(255),
	Age int NOT NULL,
	Eligibility bit NOT NULL,
);