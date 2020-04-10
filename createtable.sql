DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE moviedb;

CREATE TABLE movies(
	id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL
);

CREATE TABLE stars(
	id varchar(10) primary key,
    name varchar(100) NOT NULL,
    birthYear integer
);

CREATE TABLE stars_in_movies(
	starId VARCHAR(10) REFERENCES stars.id,	
    movieId VARCHAR(10) REFERENCES movies.id
);

CREATE TABLE genres(
	id integer PRIMARY KEY AUTO_INCREMENT,
    name varchar(32) NOT NULL
);

CREATE TABLE genres_in_movies(
	genreId INTEGER REFERENCES genres.id,
    movieId VARCHAR(10) REFERENCES movies.id
);

CREATE TABLE customers(
	id integer PRIMARY KEY AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) references creditcards.id,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL
);

CREATE TABLE sales(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customerId INTEGER REFERENCES customers.id,
    movieId VARCHAR(10) REFERENCES movies.id,
    saleDate DATE NOT NULL
);

CREATE TABLE creditcards(
	id varchar(20) primary key,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE ratings(
	movieId VARCHAR(10) REFERENCES movies.id,
    rating FLOAT NOT NULL,
    numVotes INTEGER NOT NULL
);

CREATE OR REPLACE VIEW top_movies AS(
	SELECT ratings.movieId, ratings.rating
	FROM ratings
    ORDER BY ratings.rating DESC
	LIMIT 20);

CREATE OR REPLACE VIEW movie_genres AS(
	SELECT m.id,
	substring_index(group_concat(g.name SEPARATOR ','),',',3) as genres
	FROM movies AS m,genres_in_movies AS gm, genres AS g
	WHERE gm.movieId = m.id
	AND gm.genreId = g.id
	GROUP BY m.id);

CREATE OR REPLACE VIEW movie_stars AS(
	SELECT m.id,
	substring_index(group_concat(s.name SEPARATOR ','),',',3) as actors,
	substring_index(group_concat(s.id SEPARATOR ','),',',3) as starId
	FROM movies AS m, stars_in_movies AS sm, stars AS s
	WHERE sm.movieId = m.id
	AND sm.starId = s.id
	GROUP BY m.id);
