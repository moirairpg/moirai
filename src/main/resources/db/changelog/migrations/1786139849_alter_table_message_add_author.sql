--liquibase formatted sql
--changeset moirai:1786139849_alter_table_message_add_author
--preconditions onFail:HALT, onError:HALT

ALTER TABLE message
    ADD COLUMN author_id             BIGINT,
    ADD COLUMN author_character_id   BIGINT,
    ADD COLUMN author_character_name VARCHAR;

CREATE INDEX idx_message_author_character_id ON message (author_character_id);

/* liquibase rollback
DROP INDEX idx_message_author_character_id;
ALTER TABLE message
    DROP COLUMN author_id,
    DROP COLUMN author_character_id,
    DROP COLUMN author_character_name;
*/
