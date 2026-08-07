--liquibase formatted sql
--changeset moirai:1786139857_update_table_message_backfill_author
--preconditions onFail:HALT, onError:HALT

UPDATE message m
   SET author_id             = am.player_id,
       author_character_id   = am.player_character_id,
       author_character_name = pc.name
  FROM adventure_membership am
       JOIN moirai_user u ON u.id = am.player_id
       JOIN player_character pc ON pc.id = am.player_character_id
 WHERE m.adventure_id = am.adventure_id
   AND m.created_by = u.username
   AND m.role = 'USER';

/* liquibase rollback
UPDATE message
   SET author_id             = NULL,
       author_character_id   = NULL,
       author_character_name = NULL;
*/
