-- PostgreSQL equivalent
CREATE DATABASE core;
CREATE DATABASE luckperms;

\c luckperms;

CREATE TABLE group_permissions
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(36)  NOT NULL,
    permission VARCHAR(200) NOT NULL,
    value      SMALLINT     NOT NULL,
    server     VARCHAR(36)  NOT NULL,
    world      VARCHAR(64)  NOT NULL,
    expiry     BIGINT       NOT NULL,
    contexts   VARCHAR(200) NOT NULL
);

CREATE INDEX group_permissions_name
    ON group_permissions (name);

INSERT INTO group_permissions (id, name, permission, value, server, world, expiry, contexts)
VALUES (1, 'default', '*', 1, 'global', 'global', 0, '{}');