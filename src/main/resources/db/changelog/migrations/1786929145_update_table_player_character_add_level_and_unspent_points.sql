--liquibase formatted sql
--changeset moirai:1786929145_update_table_player_character_add_level_and_unspent_points
--preconditions onFail:HALT, onError:HALT

ALTER TABLE player_character
    ADD COLUMN level                    INT NOT NULL DEFAULT 1,
    ADD COLUMN unspent_attribute_points INT NOT NULL DEFAULT 0,
    ADD COLUMN unspent_skill_points     INT NOT NULL DEFAULT 0;

/* liquibase rollback
ALTER TABLE player_character
    DROP COLUMN level,
    DROP COLUMN unspent_attribute_points,
    DROP COLUMN unspent_skill_points;
*/
