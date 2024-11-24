--liquibase formatted sql
--changeset moirai:1711197366_create_table_world
--preconditions onFail:HALT, onError:HALT

CREATE TABLE world (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR NOT NULL,
    adventure_start VARCHAR NOT NULL,
    owner_discord_id VARCHAR(100) NOT NULL,
    discord_users_allowed_to_read VARCHAR,
    discord_users_allowed_to_write VARCHAR,
    visibility VARCHAR(20) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

CREATE TABLE world_lorebook (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR NOT NULL,
    regex VARCHAR(255) NOT NULL,
    player_discord_id VARCHAR(100),
    is_player_character BOOLEAN DEFAULT FALSE NOT NULL,
    world_id VARCHAR(100) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE world_lorebook
--rollback DROP TABLE world