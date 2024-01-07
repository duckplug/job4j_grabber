CREATE SCHEMA post
CREATE TABLE IF NOT EXISTS post.post(id serial primary key,
 title text, description text, link text UNIQUE, created timestamp);
SET search_path TO post, public;