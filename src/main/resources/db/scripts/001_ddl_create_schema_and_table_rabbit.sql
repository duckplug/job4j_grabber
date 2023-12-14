CREATE SCHEMA grabber
CREATE TABLE IF NOT EXISTS rabbit(id serial primary key, created_date timestamp);
SET search_path TO grabber, public;
