DROP IF EXISTS TABLE WORK_EXPR;
DROP IF EXISTS TABLE EDUCATIONAL_DETAILS;
DROP IF EXISTS TABLE MESSAGE;
DROP IF EXISTS TABLE CONNECTION_USR;
DROP IF EXISTS TABLE USR;


CREATE TABLE USR(
	userId varchar(50) UNIQUE NOT NULL, 
	password varchar(50) NOT NULL,
	email text NOT NULL,
	name char(50),
	dateOfBirth date,
	Primary Key(userId));

CREATE TABLE WORK_EXPR(
	userId char(50) NOT NULL, 
	company char(50) NOT NULL, 
	role char(50) NOT NULL,
	location char(50),
	startDate date,
	endDate date,
	PRIMARY KEY(userId,company,role,startDate));

CREATE TABLE EDUCATIONAL_DETAILS(
	userId char(50) NOT NULL, 
	instituitionName char(50) NOT NULL, 
	major char(50) NOT NULL,
	degree char(50) NOT NULL,
	startdate date,
	enddate date,
	PRIMARY KEY(userId,major,degree));

CREATE TABLE MESSAGE(
	msgId integer UNIQUE NOT NULL, 
	senderId char(50) NOT NULL,
	receiverId char(50) NOT NULL,
	contents char(500) NOT NULL,
	sendTime timestamp,
	deleteStatus integer,
	status char(30) NOT NULL,
	PRIMARY KEY(msgId));

CREATE TABLE CONNECTION_USR(
	userId char(50) NOT NULL, 
	connectionId char(50) NOT NULL, 
	status char(50) NOT NULL,
	PRIMARY KEY(userId,connectionId));
