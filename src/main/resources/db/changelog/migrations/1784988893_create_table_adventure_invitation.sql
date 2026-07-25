--liquibase formatted sql
--changeset moirai:1784988893_create_table_adventure_invitation
--preconditions onFail:HALT, onError:HALT

CREATE TABLE adventure_invitation (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    adventure_id BIGINT NOT NULL REFERENCES adventure(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    version INT DEFAULT 0 NOT NULL,
    created_by VARCHAR(100),
    creation_date TIMESTAMPTZ(6),
    last_update_date TIMESTAMPTZ(6)
);

CREATE INDEX idx_adventure_invitation_user ON adventure_invitation(user_id);

/* liquibase rollback
DROP INDEX idx_adventure_invitation_user;
DROP TABLE adventure_invitation;
*/
