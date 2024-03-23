--liquibase formatted sql
--changeset chatrpg:1711197488_create_table_channel_config
--preconditions onFail:HALT, onError:HALT

CREATE TABLE channel_config (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    world_id VARCHAR(100) NOT NULL,
    persona_id VARCHAR(100) NOT NULL,
    moderation VARCHAR(20) NOT NULL,
    ai_model VARCHAR(50) NOT NULL,
    max_token_limit SMALLINT NOT NULL,
    message_history_size SMALLINT NOT NULL,
    temperature NUMERIC NOT NULL,
    frequency_penalty NUMERIC DEFAULT 0 NOT NULL,
    presence_penalty NUMERIC DEFAULT 0 NOT NULL,
    stop_sequences VARCHAR(255),
    logit_bias VARCHAR(255)
);

--rollback DROP TABLE channel_config