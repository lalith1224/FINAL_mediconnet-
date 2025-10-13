-- WARNING: Dev-only initializer. Drops and recreates the public schema.
-- Use with caution. Disable for production.

DO $$ BEGIN
    PERFORM 1 FROM pg_namespace WHERE nspname = 'public';
    IF FOUND THEN
        EXECUTE 'DROP SCHEMA public CASCADE';
    END IF;
END $$;

CREATE SCHEMA public;

-- Useful extensions for UUIDs and crypto functions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Let Hibernate create all tables based on JPA entities after this reset.
-- No table DDL here on purpose to avoid divergence with entities.
