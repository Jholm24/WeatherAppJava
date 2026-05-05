-- i skal selv spinne en db op
-- Create database weatherAppDB

CREATE TABLE if not exists addresses (
                           id        SERIAL PRIMARY KEY,
                           street    VARCHAR(255) NOT NULL,
                           city      VARCHAR(100) NOT NULL,
                           zip_code  VARCHAR(20)  NOT NULL,
                           country   VARCHAR(100) NOT NULL DEFAULT 'Denmark'
);