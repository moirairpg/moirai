--liquibase formatted sql
--changeset moirai:1734220446_drop_table_channel_config
--preconditions onFail:HALT, onError:HALT

DROP TABLE channel_config CASCADE;

/* liquibase rollback
CREATE TABLE channel_config (
   id VARCHAR(100) PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
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
   owner_discord_id VARCHAR(100) NOT NULL,
   discord_users_allowed_to_read VARCHAR,
   discord_users_allowed_to_write VARCHAR,
   visibility VARCHAR(20) NOT NULL,
   version INT DEFAULT 0 NOT NULL,
   creator_discord_id VARCHAR(100) NOT NULL,
   creation_date TIMESTAMP WITH TIME ZONE,
   last_update_date TIMESTAMP WITH TIME ZONE
);

INSERT INTO channel_config (
    id,
    name,
    game_mode,
    is_multiplayer,
    world_id,
    persona_id,
    discord_channel_id,
    moderation,
    ai_model,
    max_token_limit,
    temperature,
    frequency_penalty,
    presence_penalty,
    stop_sequences,
    logit_bias,
    owner_discord_id,
    discord_users_allowed_to_read,
    discord_users_allowed_to_write,
    visibility,
    version,
    creator_discord_id,
    creation_date,
    last_update_date
)
SELECT a.id,
       a.name,
       a.game_mode,
       a.is_multiplayer,
       a.world_id,
       a.persona_id,
       a.discord_channel_id,
       a.moderation,
       a.ai_model,
       a.max_token_limit,
       a.temperature,
       a.frequency_penalty,
       a.presence_penalty,
       a.stop_sequences,
       a.logit_bias,
       a.owner_discord_id,
       a.discord_users_allowed_to_read,
       a.discord_users_allowed_to_write,
       a.visibility,
       a.version,
       a.creator_discord_id,
       a.creation_date,
       a.last_update_date
  FROM adventure a;
*/