DROP DATABASE moviedb;
CREATE DATABASE moviedb;
USE moviedb;
CREATE TABLE stars(
	id varchar(10) primary key,
    name varchar(100) NOT NULL,
    birthYear integer
);
CREATE TABLE genres(
	id integer primary key,
    name varchar(32) NOT NULL
);
CREATE TABLE customers(
	id integer primary key,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) references creditcards.id,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL
);
CREATE TABLE creditcards(
	id varchar(20) primary key,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);