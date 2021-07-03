-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER some_owner_name
WITH PASSWORD 'somePassword';

GRANT ALL
ON ALL TABLES IN SCHEMA public
TO some_owner_name;

GRANT ALL
ON ALL SEQUENCES IN SCHEMA public
TO some_owner_name;

CREATE USER some_username
WITH PASSWORD 'someUserPassword';

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO some_username;

GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO some_username;
