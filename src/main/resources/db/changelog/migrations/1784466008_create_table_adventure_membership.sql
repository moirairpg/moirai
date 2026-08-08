--liquibase formatted sql
--changeset moirai:1784466008_create_table_adventure_membership
--preconditions onFail:HALT, onError:HALT

CREATE TABLE adventure_membership (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    adventure_id        BIGINT NOT NULL REFERENCES adventure(id) ON DELETE CASCADE,
    player_character_id BIGINT NOT NULL,
    player_id           BIGINT NOT NULL,
    UNIQUE (adventure_id, player_character_id),
    UNIQUE (adventure_id, player_id)
);

CREATE INDEX idx_adventure_membership_player_character_id
    ON adventure_membership (player_character_id, adventure_id);

/* liquibase rollback
DROP TABLE adventure_membership;
*/
