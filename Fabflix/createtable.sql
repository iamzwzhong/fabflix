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

CREATE TABLE employees(
	email VARCHAR(50) primary key,
    password VARCHAR(20) NOT NULL,
    fullname VARCHAR(100)
);

CREATE OR REPLACE VIEW movie_genres AS(
	SELECT m.id,
	substring_index(group_concat(g.name SEPARATOR ','),',',3) as genres
	FROM movies AS m,genres_in_movies AS gm, genres AS g
	WHERE gm.movieId = m.id
	AND gm.genreId = g.id
	GROUP BY m.id);
    
CREATE OR REPLACE VIEW movie_stars_sorted AS(
	SELECT COUNT(*) AS cnt, sim.starId AS id
	FROM stars_in_movies AS sim
    GROUP BY sim.starId ORDER BY cnt DESC
);

CREATE OR REPLACE VIEW movie_stars AS(
SELECT m.id,
substring_index(group_concat(smt.name ORDER by smt.cnt DESC, smt.name SEPARATOR ','),',',3) as actors,
substring_index(group_concat(smt.name SEPARATOR ','),',',100) as allactors,
substring_index(group_concat(smt.id SEPARATOR ','),',',3) as starId
FROM movies AS m, stars_in_movies AS sm, 
(SELECT s.id, s.name, s.birthYear, mss.cnt FROM stars s
INNER JOIN movie_stars_sorted mss ON mss.id = s.id
) as smt
WHERE sm.movieId = m.id
AND sm.starId = smt.id
GROUP BY m.id);

CREATE INDEX idx_starId
ON stars_in_movies (starId);

CREATE INDEX idx_movieId
ON stars_in_movies (movieId);