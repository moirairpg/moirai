--liquibase formatted sql
--changeset moirai:1786888515_update_table_message_add_action_outcome
--preconditions onFail:HALT, onError:HALT

ALTER TABLE message
    ADD COLUMN action_outcome VARCHAR NULL,
    ADD COLUMN action_target  VARCHAR NULL;

/* liquibase rollback
ALTER TABLE message
    DROP COLUMN action_outcome,
    DROP COLUMN action_target;
*/
