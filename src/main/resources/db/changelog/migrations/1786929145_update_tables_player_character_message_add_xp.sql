--liquibase formatted sql
--changeset moirai:1786929145_update_tables_player_character_message_add_xp
--preconditions onFail:HALT, onError:HALT

ALTER TABLE message
    ADD COLUMN action_xp_awarded INT NULL;

/* liquibase rollback
ALTER TABLE message
    DROP COLUMN action_xp_awarded;
*/
