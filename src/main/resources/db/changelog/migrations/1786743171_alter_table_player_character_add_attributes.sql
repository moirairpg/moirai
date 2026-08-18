--liquibase formatted sql
--changeset moirai:1786743171_alter_table_player_character_add_attributes
--preconditions onFail:HALT, onError:HALT

ALTER TABLE player_character
        ADD COLUMN attributes JSONB NOT NULL DEFAULT '{"strength": 1, "agility": 1, "vigor": 1, "intelligence": 1, "awareness": 1, "charisma": 1}'::jsonb;

ALTER TABLE player_character
      ALTER COLUMN attributes DROP DEFAULT;

/* liquibase rollback
ALTER TABLE player_character
       DROP COLUMN attributes;
*/
