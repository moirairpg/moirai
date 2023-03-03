INSERT INTO lorebook(id, entry_name, player_discord_id, entry_description) VALUES(
    '66daa244-aa28-4299-923d-fe5267cc9a0f',
    'Aurelia',
    '191672198233063425',
    'Human bard with a slender frame and a warm smile. Her quick wit and charming personality make her a natural leader. She is proud of her elvish heritage, which gives her a graceful and fluid movement. She wears a red and gold cloak, and carries a lute with her at all times. She is skilled musician and storyteller. Her songs have been known to inspire courage and hope in those who listen to her.'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), 'Aurelia', '66daa244-aa28-4299-923d-fe5267cc9a0f');

INSERT INTO lorebook(id, entry_name, player_discord_id, entry_description) VALUES(
    '94f64055-2d02-4946-8e2e-47706aa0fd7d',
    'Sirta',
    '274040935158120448',
    'She a young female human warlock. Daughter of a very powerful family in the city. Has reputation for being rebellious and selfish, but is also a capable warlock and a skilled leader. She often causes trouble with her friends and allies by teasing them. Her mentor, Lelena, taught her to harness magic from unfathomable cosmic forces. Sirta and Lelena were both captured by the Empire and tortured until they escaped. Sirta is adventuring while searching for her father''s missing books.'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), 'Sirta', '94f64055-2d02-4946-8e2e-47706aa0fd7d');

INSERT INTO lorebook(id, entry_name, player_discord_id, entry_description) VALUES(
    '1642d9fb-b70a-448f-90d0-c3f6aa265062',
    'Miraj',
    '208457481494593536',
    'Miraj is a curious creature, in fact they are an enchanted ball of clay that is as large as a melon and weighs nine pounds. Miraj is given animus by a spirit and serves as that spirit''s vessel. Miraj can reshape its spherical clay form into almost any object of similar mass. While transformed into an object, the magical clay that forms Miraj will take on the physical properties and appearance of that object. Miraj strongly prefers to take on the forms of objects instead of creatures. The spirit inhabiting Miraj was a jester in their past life before being bound to Miraj, and Miraj''s personality is an extension of the jester''s spirit which identifies as they/them. Miraj''s true inner workings are a mystery, however Miraj behaves in prankish and humorous ways. Miraj has no possessions, cannot gesticulate, and has no senses due to being a ball of clay. Miraj relies upon supernatural perception akin to the five primary senses.'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), 'Miraj', '1642d9fb-b70a-448f-90d0-c3f6aa265062');

INSERT INTO lorebook(id, entry_name, entry_description) VALUES(
    'a9d134c0-0c27-44aa-9ce7-406940859a31',
    'Janello',
    'Janello is an Orc that likes to slice people with his sword because he''s a big bad bandit'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), 'Janello', 'a9d134c0-0c27-44aa-9ce7-406940859a31');

INSERT INTO lorebook(id, entry_name, entry_description) VALUES(
    '9ffcd93b-65fb-4232-a619-76eb2d8d255d',
    'Dark Book',
    'The Dark Book is an ancient tome of dark magicks that takes over the user and turns them into a very power being... but also a very evil being. The book was written by Archmage Marinor.'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), '((dark|the) book)', '9ffcd93b-65fb-4232-a619-76eb2d8d255d');

INSERT INTO lorebook(id, entry_name, entry_description) VALUES(
    '2716d623-14c0-4bce-8d71-5995b8b3533f',
    'Asmalakhan',
    'The Asmalakhan are being of extreme power that were created by Archmage Marinor, and they will do the bidding of whoever holds the Dark Book.'
);
INSERT INTO lorebook_regex(id, regex, lorebook_id) VALUES((SELECT gen_random_uuid()), '(asmalakhan|creature statues)', '2716d623-14c0-4bce-8d71-5995b8b3533f');
