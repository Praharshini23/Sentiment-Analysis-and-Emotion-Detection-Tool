CREATE DATABASE SentimentDB;
USE SentimentDB;
CREATE TABLE SentimentResults (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input_text TEXT NOT NULL,
    sentiment VARCHAR(10) NOT NULL
);

SELECT * FROM SentimentResults;
TRUNCATE TABLE SentimentResults;
describe SentimentResults;
DELETE FROM SentimentResults WHERE id=13;
