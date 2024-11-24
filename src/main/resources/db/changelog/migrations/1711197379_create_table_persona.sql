--liquibase formatted sql
--changeset moirai:1711197379_create_table_persona
--preconditions onFail:HALT, onError:HALT

CREATE TABLE persona (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    personality VARCHAR NOT NULL,
    nudge_content VARCHAR,
    nudge_role VARCHAR(25),
    bump_content VARCHAR,
    bump_role VARCHAR(25),
    bump_frequency SMALLINT,
    owner_discord_id VARCHAR(100) NOT NULL,
    discord_users_allowed_to_read VARCHAR,
    discord_users_allowed_to_write VARCHAR,
    visibility VARCHAR(20) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE persona