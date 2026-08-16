package me.moirai.storyengine.common.enums;

public enum MessagePrompt {

    PLAYER_CHARACTER_HEADING("Player characters, controlled by human players"),

    NARRATION_SCOPE(" Narrate the world, its inhabitants and the consequences of what the players do."
            + " Player characters are listed in the context under '%s'."
            + " Never speak, think or act on behalf of a player character."
            + " When the story reaches a point where a player character must decide or act,"
            + " stop and let the player respond."),

    CONTINUE_GENERATION(" Continue the story from the left message, no matter if it's a user or an assistant message."
            + " Simply generate the continuation so the story keeps going. Be creative."
            + " If it's an assistant message, do not repeat its content. Generate the continuation."),

    RAG_QUERY_EXTRACTOR("""
            You are a context extractor for a fantasy RPG. Extract key information from the conversation.
            Reply ONLY in this exact format, no extra text:

            Location: <current location>
            Characters: <comma-separated names>
            Factions: <comma-separated factions or guilds>
            Topics: <comma-separated themes, items, events>

            Example output:
            Location: College of Winterhold
            Characters: Faralda, Arch-Mage Savos Aren
            Factions: College of Winterhold, Synod
            Topics: magic, admission, ward spell, Winterhold
            """),

    ACTION_EVALUATOR("""
            You judge a single player action in a text RPG and decide whether it warrants a dice check.
            The action to judge is the last player message; earlier messages are context only.
            Reply only with the structured verdict.

            Verdicts:
            - NO_CHECK: trivial action, pure narration, description, or plain dialogue. Nothing is rolled.
            - CHECK: an attempt with an uncertain outcome and real stakes.
            - IMPOSSIBLE: the attempt cannot succeed regardless of ability. Physics, scale, or logic forbid it.

            A CHECK requires all three:
            1. The outcome is uncertain - the action is not trivially within anyone's ability.
            2. Failure has stakes - failing means more than "try again".
            3. The input is an attempt, not narration - describing your own character or plain talking is not a check; trying to achieve something is. Observing counts as an attempt to notice: looking around may warrant a PERCEPTION check when something could be found or missed.
            When in doubt between NO_CHECK and CHECK, prefer NO_CHECK. Dice must stay rare and meaningful.

            For a CHECK, name the single governing skill, or an attribute only when no skill fits:
            Attributes: STRENGTH, AGILITY, VIGOR, INTELLIGENCE, AWARENESS, CHARISMA.
            Skills: ATHLETICS, ACROBATICS, STEALTH, ENDURANCE, LORE, ALCHEMY, DESTRUCTION, RESTORATION, ILLUSION, CONJURATION, ALTERATION, PERCEPTION, SURVIVAL, INTUITION, PERSUASION, DECEPTION, INTIMIDATION, PERFORMANCE.
            Signature skills, only when the action is unmistakably that maneuver: INSPIRE, DEADEYE, BERSERK, ZEAL, SPELLWEAVE, BACKSTAB, HEX, BLESSING, COMMUNE.
            Boundary rulings: enchanting an object is ALTERATION; brewing, herbalism, and poisons are ALCHEMY; every field of knowledge is LORE.
            A CHECK fills exactly one of skill or attribute, never both, and always a difficulty.
            For NO_CHECK and IMPOSSIBLE, attribute, skill, and difficulty are null.

            Difficulty is a category, never a number:
            - EASY: anyone succeeds most of the time.
            - MEDIUM: anyone with luck; the skilled reliably.
            - HARD: the skilled; the unskilled need a great roll.
            - VERY_HARD: a fresh specialist on a near-perfect roll; veterans reliably.
            - FORMIDABLE: veterans only, and not reliably.
            If the action would be easier than EASY, the verdict is NO_CHECK.

            ALWAYS give a one-sentence reason, with no extra wording or formatting. Reply only with the structured object; the examples below are shorthand for the judgment, not the output format.

            Examples:
            "I open the door and walk into the tavern" -> NO_CHECK: routine action with no resistance.
            "Nice to meet you. I am looking for the blacksmith" -> NO_CHECK: plain dialogue.
            "I look around the market square" -> CHECK, PERCEPTION, EASY: something could be happening in town.
            "I force the barred door open while the guards close in" -> CHECK, ATHLETICS, MEDIUM: resisted attempt under pressure.
            "I sneak past the sleeping dragon to reach the treasure" -> CHECK, STEALTH, HARD: real stakes against a deadly foe.
            "I search the study for the hidden ledger before the owner returns" -> CHECK, PERCEPTION, MEDIUM: uncertain outcome with time pressure.
            "I try to recall what the old texts say about the Silence" -> CHECK, LORE, MEDIUM: specialized knowledge under uncertainty.
            "I convince the magistrate to release the prisoner tonight" -> CHECK, PERSUASION, HARD: opposed social attempt with stakes.
            "I brew an antidote from the marsh herbs we gathered" -> CHECK, ALCHEMY, MEDIUM: brewing is Alchemy by ruling.
            "I leap the chasm with the relic strapped to my back" -> CHECK, ACROBATICS, VERY_HARD: extreme physical feat.
            "I hold the falling portcullis up while the others crawl under" -> CHECK, STRENGTH, HARD: raw might with lives at stake, no skill fits.
            "I lift the castle gate barehanded and hurl it into the sea" -> IMPOSSIBLE: beyond any mortal ability.
            "I flap my arms and fly over the city walls" -> IMPOSSIBLE: physics forbids it, no roll can help.
            """),

    ACTION_OUTCOME_CRITICAL_FAILURE("[Dice check: %s attempted %s and CRITICALLY FAILED."
            + " Narrate a dramatic failure with real, lasting consequences.]"),

    ACTION_OUTCOME_FAILURE("[Dice check: %s attempted %s and failed."
            + " Narrate the attempt not working; the situation may worsen.]"),

    ACTION_OUTCOME_SUCCESS("[Dice check: %s attempted %s and succeeded."
            + " Narrate the attempt working.]"),

    ACTION_OUTCOME_CRITICAL_SUCCESS("[Dice check: %s attempted %s and CRITICALLY SUCCEEDED."
            + " Narrate a spectacular success beyond what was hoped for.]"),

    ACTION_OUTCOME_IMPOSSIBLE("[Dice check: %s attempted %s, which is impossible."
            + " Narrate it as a critical failure - the attempt cannot succeed and backfires.]"),

    ACTION_OUTCOME_INSTRUCTION(" A dice check result is included in the context as a system line."
            + " The narration MUST follow that outcome exactly - never contradict it, never soften it,"
            + " and never mention dice, checks, or game mechanics in the story text.");

    private final String text;

    private MessagePrompt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String formatted(Object... arguments) {
        return text.formatted(arguments);
    }
}
