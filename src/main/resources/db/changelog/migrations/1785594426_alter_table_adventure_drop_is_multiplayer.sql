--liquibase formatted sql
--changeset moirai:1785594426_alter_table_adventure_drop_is_multiplayer
--preconditions onFail:HALT, onError:HALT

ALTER TABLE adventure
DROP COLUMN is_multiplayer;

/* liquibase rollback
ALTER TABLE adventure
ADD COLUMN is_multiplayer BOOLEAN NOT NULL DEFAULT FALSE
*/
