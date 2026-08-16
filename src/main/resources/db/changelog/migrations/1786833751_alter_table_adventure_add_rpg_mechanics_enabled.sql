--liquibase formatted sql
--changeset moirai:1786833751_alter_table_adventure_add_rpg_mechanics_enabled
--preconditions onFail:HALT, onError:HALT

ALTER TABLE adventure
        ADD COLUMN rpg_mechanics_enabled BOOLEAN NOT NULL DEFAULT TRUE;

/* liquibase rollback
ALTER TABLE adventure
       DROP COLUMN rpg_mechanics_enabled;
*/
