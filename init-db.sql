-- Initialize SOA database schema
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create coordinates table
CREATE TABLE IF NOT EXISTS coordinates (
    id BIGSERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y DOUBLE PRECISION NOT NULL
);

-- Create person table
CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    birthday DATE,
    height DOUBLE PRECISION NOT NULL CHECK (height > 0),
    weight BIGINT NOT NULL CHECK (weight > 0),
    passport_id VARCHAR NOT NULL
);

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    oscars_count BIGINT CHECK (oscars_count > 0),
    golden_palm_count BIGINT CHECK (golden_palm_count > 0),
    budget REAL CHECK (budget > 0),
    genre VARCHAR NOT NULL,
    coordinates_id BIGINT REFERENCES coordinates(id),
    screenwriter_id BIGINT REFERENCES person(id)
);

-- Create oscars table for backend-oscars
CREATE TABLE IF NOT EXISTS oscars (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    award_type VARCHAR NOT NULL,
    year INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO coordinates (x, y) VALUES 
(100, 200.5),
(150, 300.7),
(200, 400.9);

INSERT INTO person (name, birthday, height, weight, passport_id) VALUES 
('John Doe', '1980-01-15', 175.5, 70, 'PASS001'),
('Jane Smith', '1985-03-22', 165.0, 55, 'PASS002'),
('Bob Johnson', '1975-07-10', 180.0, 80, 'PASS003');

INSERT INTO movies (name, creation_date, oscars_count, golden_palm_count, budget, genre, coordinates_id, screenwriter_id) VALUES 
('The Great Movie', '2020-01-01', 3, 1, 1000000.0, 'DRAMA', 1, 1),
('Action Hero', '2021-06-15', 1, 0, 2000000.0, 'ACTION', 2, 2),
('Comedy Gold', '2022-03-10', 0, 0, 500000.0, 'COMEDY', 3, 3);
