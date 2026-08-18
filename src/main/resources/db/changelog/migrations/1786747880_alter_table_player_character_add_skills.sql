--liquibase formatted sql
--changeset moirai:1786747880_alter_table_player_character_add_skills
--preconditions onFail:HALT, onError:HALT

ALTER TABLE player_character
        ADD COLUMN skills JSONB;

UPDATE player_character
   SET skills = '{
           "athletics": 0,
           "acrobatics": 0,
           "stealth": 0,
           "endurance": 0,
           "lore": 0,
           "alchemy": 0,
           "destruction": 0,
           "restoration": 0,
           "illusion": 0,
           "conjuration": 0,
           "alteration": 0,
           "perception": 0,
           "survival": 0,
           "intuition": 0,
           "persuasion": 0,
           "deception": 0,
           "intimidation": 0,
           "performance": 0,
           "signature": 1
       }'::jsonb
       || CASE character_class
              WHEN 'BARD' THEN '{"performance": 2, "persuasion": 2}'::jsonb
              WHEN 'RANGER' THEN '{"survival": 2, "perception": 2}'::jsonb
              WHEN 'BARBARIAN' THEN '{"athletics": 2, "endurance": 2}'::jsonb
              WHEN 'PALADIN' THEN '{"persuasion": 2, "endurance": 2}'::jsonb
              WHEN 'MAGE' THEN '{"destruction": 2, "conjuration": 2}'::jsonb
              WHEN 'ROGUE' THEN '{"stealth": 2, "acrobatics": 2}'::jsonb
              WHEN 'WITCH' THEN '{"illusion": 2, "alchemy": 2}'::jsonb
              WHEN 'CLERIC' THEN '{"restoration": 2, "persuasion": 2}'::jsonb
              ELSE '{"signature": 0}'::jsonb
          END;

ALTER TABLE player_character
      ALTER COLUMN skills SET NOT NULL;

/* liquibase rollback
ALTER TABLE player_character
       DROP COLUMN skills;
*/
