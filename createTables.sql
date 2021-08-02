DROP TABLE IF EXISTS child;
DROP TABLE IF EXISTS parent;
DROP TABLE IF EXISTS transfer;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS mood;
DROP TABLE IF EXISTS family;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS parent_xchge;
DROP TABLE IF EXISTS category_transfer;
DROP TABLE IF EXISTS dispostion_transfer;

CREATE TABLE child (
	cid 	serial,
	fname 	varchar(30) not null,
	lname 	varchar(30) not null,
	dob		date,
	gender	varchar(6),
	primary key (cid),
	constraint gender_check check (gender in ('MALE', 'FEMALE', 'Other'))
);

CREATE TABLE parent (
	pid		serial,
	fname	varchar(30) not null,
	lname	varchar(30)	not null,
	dob		date,
	gender	varchar(6),
	primary key (pid),
	constraint gender_check check (gender in ('Male', 'Female', 'Other'))
);

CREATE TABLE transfer(
	tid			serial,
	dateOfEntry	date,
	timeOfEntry	time,
	primary key (tid)	
);

CREATE TABLE category(
	name		varchar(20),
	description	varchar(40),
	primary key (name)
);

CREATE TABLE mood(
	name		varchar(20),
	description	varchar(40),
	primary key (name)
);

CREATE TABLE family(
	cid	integer,
	pid integer,
	foreign key(cid) references child(cid),
	foreign key(pid) references parent(pid)
);

CREATE TABLE attendance(
	tid	integer,
	cid integer,
	foreign key(tid) references transfer(tid),
	foreign key(cid) references child(cid)
);

CREATE TABLE parent_xchge(
	tid integer,
	pid integer,
	foreign key(tid) references transfer(tid),
	foreign key(pid) references parent(pid)
);

CREATE TABLE category_transfer(
	tid				integer,
	category_name	varchar(20),
	foreign key(tid) references transfer(tid),
	foreign key(category_name) references category(name)
);

CREATE TABLE disposition_transfer(
	tid 		integer,
	mood_name	varchar(20),
	foreign key(tid) references transfer(tid),
	foreign key(mood_name) references mood(name)
);
