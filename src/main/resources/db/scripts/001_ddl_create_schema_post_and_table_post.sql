CREATE SCHEMA post
CREATE TABLE IF NOT EXISTS post(id serial primary key, name text, text text, link text UNIQUE, created_date timestamp);
SET search_path TO post, public;
