USE demo;

CREATE TABLE SerializeObjs (
    id int IDENTITY(1,1) PRIMARY KEY,
    content varchar(255),
    serialized_content VARBINARY(max)
);