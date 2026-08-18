--liquibase formatted sql
--changeset moirai:1786914307_alter_table_character_add_xp
--preconditions onFail:HALT, onError:HALT

ALTER TABLE player_character
    ADD COLUMN xp INT NOT NULL DEFAULT 0;

/* liquibase rollback
ALTER TABLE player_character
    DROP COLUMN xp;
*/
