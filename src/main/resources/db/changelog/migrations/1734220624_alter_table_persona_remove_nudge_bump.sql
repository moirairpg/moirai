--liquibase formatted sql
--changeset moirai:1734220624_alter_table_persona_remove_nudge_bump
--preconditions onFail:HALT, onError:HALT

ALTER TABLE persona
DROP COLUMN nudge_content,
DROP COLUMN nudge_role,
DROP COLUMN bump_content,
DROP COLUMN bump_role,
DROP COLUMN bump_frequency;

/* liquibase rollback
ALTER TABLE persona
 ADD COLUMN nudge_content VARCHAR,
 ADD COLUMN nudge_role VARCHAR,
 ADD COLUMN bump_content VARCHAR,
 ADD COLUMN bump_role VARCHAR,
 ADD COLUMN bump_frequency SMALLINT;

UPDATE persona p
   SET nudge_content = a.nudge,
       nudge_role = 'SYSTEM',
       bump_content = a.bump,
       bump_role = 'SYSTEM',
       bump_frequency = a.bump_frequency
  FROM adventure a
 WHERE p.id = a.persona_id;
*/