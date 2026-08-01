--liquibase formatted sql
--changeset moirai:1785619798_drop_table_persona_backups
--preconditions onFail:HALT, onError:HALT

DROP TABLE persona_permissions_backup;
DROP TABLE persona_backup;

/* liquibase rollback
CREATE TABLE persona_backup (
    nano_id    VARCHAR(100),
    uuid       UUID,
    numeric_id BIGINT
);

CREATE TABLE persona_permissions_backup (
    persona_id             BIGINT,
    owner_id               VARCHAR(100),
    users_allowed_to_read  VARCHAR,
    users_allowed_to_write VARCHAR
);
*/
