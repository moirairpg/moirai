--liquibase formatted sql
--changeset moirai:1784466009_migrate_adventure_lorebook_player_characters
--preconditions onFail:HALT, onError:HALT

INSERT INTO player_character (
    public_id, name, player_id, personality, physical_description,
    character_class, version, created_by, creation_date, last_update_date
)
SELECT al.public_id,
       al.name,
       u.id,
       al.description,
       '',
       NULL,
       0,
       al.created_by,
       al.creation_date,
       al.last_update_date
  FROM adventure_lorebook al
       JOIN moirai_user u ON u.discord_id = al.player_id
 WHERE al.is_player_character = TRUE;

DELETE FROM adventure_lorebook
 WHERE is_player_character = TRUE;

--rollback DELETE FROM player_character;
