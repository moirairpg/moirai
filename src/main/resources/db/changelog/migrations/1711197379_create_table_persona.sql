--liquibase formatted sql
--changeset chatrpg:1711197379_create_table_persona
--preconditions onFail:HALT, onError:HALT

CREATE TABLE persona (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    personality VARCHAR(255) NOT NULL,
    nudge_content VARCHAR(255),
    nudge_role VARCHAR(255),
    bump_content VARCHAR(255),
    bump_role VARCHAR(255),
    bump_frequency SMALLINT
);

--rollback DROP TABLE persona