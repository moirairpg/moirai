--liquibase formatted sql
--changeset chatrpg:1711197366_create_table_world
--preconditions onFail:HALT, onError:HALT

CREATE TABLE world (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    adventure_start VARCHAR(255) NOT NULL
);

CREATE TABLE world_lorebook (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    regex VARCHAR(255) NOT NULL,
    player_discord_id VARCHAR(100),
    is_player_character BOOLEAN DEFAULT FALSE NOT NULL,
    world_id VARCHAR(100) NOT NULL
);

--rollback DROP TABLE world
--rollback DROP TABLE world_lorebook