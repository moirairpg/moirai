--liquibase formatted sql
--changeset moirai:1784470829_alter_table_adventure_lorebook_delete_player_columns
--preconditions onFail:HALT, onError:HALT

ALTER TABLE adventure_lorebook
DROP COLUMN player_id,
DROP COLUMN is_player_character;

/* liquibase rollback
ALTER TABLE adventure_lorebook
ADD COLUMN player_id BIGINT,
ADD COLUMN is_player_character BOOLEAN NOT NULL DEFAULT FALSE
*/
