--liquibase formatted sql
--changeset moirai:1733616283_create_table_adventure
--preconditions onFail:HALT, onError:HALT

CREATE TABLE adventure (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR,
    adventure_start VARCHAR NOT NULL,
    game_mode VARCHAR(10),
    is_multiplayer BOOLEAN NOT NULL,
    world_id VARCHAR(100) NOT NULL,
    persona_id VARCHAR(100) NOT NULL,
    discord_channel_id VARCHAR(100) UNIQUE NOT NULL,
    moderation VARCHAR(20) NOT NULL,
    ai_model VARCHAR(50) NOT NULL,
    max_token_limit SMALLINT NOT NULL,
    temperature NUMERIC NOT NULL,
    frequency_penalty NUMERIC DEFAULT 0 NOT NULL,
    presence_penalty NUMERIC DEFAULT 0 NOT NULL,
    stop_sequences VARCHAR,
    logit_bias VARCHAR,
    authors_note VARCHAR,
    remember VARCHAR,
    nudge VARCHAR,
    bump VARCHAR,
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

CREATE TABLE adventure_lorebook (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR NOT NULL,
    regex VARCHAR(255) NOT NULL,
    player_discord_id VARCHAR(100),
    is_player_character BOOLEAN DEFAULT FALSE NOT NULL,
    adventure_id VARCHAR(100) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE adventure_lorebook
--rollback DROP TABLE adventure
