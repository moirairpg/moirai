--liquibase formatted sql
--changeset chatrpg:create_sample_table
--preconditions onFail:HALT, onError:HALT

CREATE TABLE sample_table (
    a_column VARCHAR(255)
);

--rollback DROP TABLE sample_table