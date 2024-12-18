--liquibase formatted sql
--changeset moirai:1734218870_migrate_channel_configs_to_adventures
--preconditions onFail:HALT, onError:HALT

INSERT INTO adventure (
    id,
    name,
    description,
    adventure_start,
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
    authors_note,
    remember,
    nudge,
    bump,
    bump_frequency,
    owner_discord_id,
    discord_users_allowed_to_read,
    discord_users_allowed_to_write,
    visibility,
    version,
    creator_discord_id,
    creation_date,
    last_update_date
)
SELECT cc.id,
       cc.name,
       w.description,
       w.adventure_start,
       cc.game_mode,
       cc.is_multiplayer,
       cc.world_id,
       cc.persona_id,
       cc.discord_channel_id,
       cc.moderation,
       cc.ai_model,
       cc.max_token_limit,
       cc.temperature,
       cc.frequency_penalty,
       cc.presence_penalty,
       cc.stop_sequences,
       cc.logit_bias,
       NULL,
       NULL,
       p.nudge_content,
       p.bump_content,
       p.bump_frequency,
       cc.owner_discord_id,
       cc.discord_users_allowed_to_read,
       cc.discord_users_allowed_to_write,
       cc.visibility,
       0,
       cc.creator_discord_id,
       cc.creation_date,
       cc.last_update_date
  FROM channel_config cc
       JOIN world w ON cc.world_id = w.id
       JOIN persona p ON cc.persona_id = p.id;

INSERT INTO adventure_lorebook (
    id,
    name,
    description,
    regex,
    player_discord_id,
    is_player_character,
    adventure_id,
    version,
    creator_discord_id,
    creation_date,
    last_update_date
)
SELECT wl.id,
       wl.name,
       wl.description,
       wl.regex,
       wl.player_discord_id,
       wl.is_player_character,
       a.id,
       wl.version,
       wl.creator_discord_id,
       wl.creation_date,
       wl.last_update_date
  FROM world_lorebook wl
       JOIN adventure a ON wl.world_id = a.world_id;

--rollback DELETE FROM adventure;
--rollback DELETE FROM adventure_lorebook;
