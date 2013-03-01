drop table alarm;
drop table user_in_group;
drop table userrecord;

CREATE TABLE UserRecord (
  Email VARCHAR(128) NOT NULL,
  Nickname VARCHAR(128) NOT NULL,
  FirstName Varchar(128) NOT NULL,
  LastName VarChar(128) NOT NULL,
  Password CHAR(128) NOT NULL,
  Salt CHAR(128) NOT NULL,
  PRIMARY KEY (Email));
  
CREATE TABLE User_in_Group (
  Email VARCHAR(128) NOT NULL,
  GroupName Varchar(64) NOT NULL,
  PRIMARY KEY (Email, GroupName),
  FOREIGN KEY (Email) REFERENCES UserRecord(Email));
  
CREATE TABLE Alarm (
  Id Integer GENERATED ALWAYS AS IDENTITY NOT NULL,
  Description VARCHAR(256) NOT NULL,
  NextAlarm Date,
  RepeatDelayDays Integer,
  RepeatUntil Date,
  RepeatTimes Integer,
  Owner Varchar(128) NOT NULL,
  PRIMARY KEY (Id),
  FOREIGN KEY (Owner) REFERENCES UserRecord(Email));

