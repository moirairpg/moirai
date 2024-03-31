--liquibase formatted sql
--changeset chatrpg:1711197488_create_table_channel_config
--preconditions onFail:HALT, onError:HALT

CREATE TABLE channel_config (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    world_id VARCHAR(100) NOT NULL,
    persona_id VARCHAR(100) NOT NULL,
    discord_channel_id VARCHAR(100) UNIQUE NOT NULL,
    moderation VARCHAR(20) NOT NULL,
    ai_model VARCHAR(50) NOT NULL,
    max_token_limit SMALLINT NOT NULL,
    message_history_size SMALLINT NOT NULL,
    temperature NUMERIC NOT NULL,
    frequency_penalty NUMERIC DEFAULT 0 NOT NULL,
    presence_penalty NUMERIC DEFAULT 0 NOT NULL,
    stop_sequences VARCHAR(255),
    logit_bias VARCHAR(255),
    owner_discord_id VARCHAR(100) NOT NULL,
    discord_users_allowed_to_read VARCHAR(255),
    discord_users_allowed_to_write VARCHAR(255),
    visibility VARCHAR(20) NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE channel_config