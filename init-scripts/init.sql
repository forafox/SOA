CREATE DATABASE filmsdb;
CREATE DATABASE oscarsdb;
CREATE DATABASE keycloakdb;


\connect filmsdb;
CREATE TABLE coordinates (
    id BIGSERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y DOUBLE PRECISION NOT NULL
);

CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    birthday DATE,
    height DOUBLE PRECISION,
    weight BIGINT,
    passport_id VARCHAR(50)
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creation_date DATE NOT NULL,
    oscars_count BIGINT,
    golden_palm_count BIGINT,
    budget REAL,
    genre VARCHAR(50) NOT NULL,
    coordinates_id BIGINT REFERENCES coordinates(id) ON DELETE CASCADE,
    screenwriter_id BIGINT REFERENCES person(id) ON DELETE CASCADE
);
