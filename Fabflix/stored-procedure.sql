USE moviedb;

DELIMITER $$
CREATE PROCEDURE `add_movie`(IN m_id VARCHAR(10), IN m_title VARCHAR(100), IN m_year INT, IN m_director VARCHAR(100), IN s_name VARCHAR(100), IN g_name VARCHAR(32))
BEGIN
    DECLARE s_exist INT;
    DECLARE g_exist INT;
    DECLARE s_id VARCHAR(10);
    DECLARE g_id INT;
    
    SELECT EXISTS(select * from stars where name = s_name) INTO s_exist;
    SELECT EXISTS(select * from genres where name = g_name) INTO g_exist;
    
    SELECT id from stars where name = s_name INTO s_id;
    
    IF (s_exist = 0) THEN
        SELECT max(id) FROM stars INTO s_id;
        SELECT SUBSTRING(s_id, 3) INTO s_id;
        SET s_id = s_id + 1;
        SELECT CAST(s_id as UNSIGNED) INTO s_id;
        SELECT CONCAT('nm',CAST(s_id AS CHAR)) INTO s_id;
        INSERT INTO stars(id, name) VALUES (s_id, s_name);
    END IF;
    IF (g_exist = 0) THEN
        INSERT INTO genres(name) VALUES (g_name);
    END IF;
    
    SELECT id FROM genres WHERE name = g_name INTO g_id;
    
    INSERT INTO movies(id, title, year, director) VALUES (m_id, m_title, m_year, m_director);
    
    INSERT INTO stars_in_movies(starId, movieId) VALUES (s_id, m_id);
    INSERT INTO genres_in_movies(genreId, movieId) VALUES (g_id, m_id);
    
END$$
DELIMITER ;