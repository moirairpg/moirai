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
    bump_frequency SMALLINT,
    game_mode VARCHAR(10),
    owner_discord_id VARCHAR(100) NOT NULL,
    discord_users_allowed_to_read VARCHAR(255),
    discord_users_allowed_to_write VARCHAR(255),
    visibility VARCHAR(20) NOT NULL,
    creator_discord_id VARCHAR(100) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE,
    last_update_date TIMESTAMP WITH TIME ZONE
);

--rollback DROP TABLE persona