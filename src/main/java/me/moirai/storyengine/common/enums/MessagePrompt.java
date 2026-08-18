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
            The acting character is described at the start of the context. Judge what is possible and how hard it is from who they are - their nature, powers, and the story's reality - never from their skill ratings; the dice already handle competence. What is impossible for one nature is routine for another.
            Reply only with the structured verdict.

            Verdicts:
            - NO_CHECK: trivial action, pure narration, description, or plain dialogue. Nothing is rolled.
            - CHECK: an attempt with an uncertain outcome and real stakes.
            - IMPOSSIBLE: the attempt cannot succeed for THIS character regardless of luck. Physics, scale, logic, or their nature forbid it.

            A CHECK requires all three:
            1. The outcome is uncertain - the action is not trivially within anyone's ability.
            2. Failure has stakes - failing means more than "try again".
            3. The input is an attempt, not narration - describing your own character or plain talking is not a check; trying to achieve something is. Observing counts as an attempt to notice: looking around may warrant a PERCEPTION check when something could be found or missed.
            When in doubt between NO_CHECK and CHECK, prefer NO_CHECK. Dice must stay rare and meaningful.

            Choosing the target:
            Match the action's METHOD - how the character attempts it - to a domain. Never pick a target because its name resembles the action's wording, and never assume circumstances the story has not established.
            1. A pool skill whose domain governs the method:
            ATHLETICS: climbing, swimming, jumping, forcing things open. ACROBATICS: balance, tumbling, dodging. MELEE: armed close combat with weapons. MARKSMANSHIP: ranged weapons - bows, crossbows, thrown. BRAWL: unarmed fighting - punches, grapples, holds. STEALTH: moving unseen and unheard, hiding. ENDURANCE: resisting exhaustion, pain, poison, harsh conditions. LORE: recalling knowledge of any field. ALCHEMY: brewing, herbalism, poisons. DESTRUCTION: offensive magic. RESTORATION: healing magic. ILLUSION: magic that deceives the senses. CONJURATION: magic that summons or creates. ALTERATION: magic that transforms or enchants. PERCEPTION: noticing, spotting, searching. SURVIVAL: tracking, foraging, navigating the wild. INTUITION: reading people and situations. PERSUASION: convincing honestly. DECEPTION: lying and misleading. INTIMIDATION: coercing through fear. PERFORMANCE: entertaining an audience.
            2. An attribute, when no skill's domain fits - raw feats with no craft behind them.
            STRENGTH: raw force - lifting, holding, breaking. AGILITY: speed and precision - quick reactions, split-second moves. VIGOR: raw toughness. INTELLIGENCE: raw reasoning. AWARENESS: raw senses and presence of mind. CHARISMA: raw force of personality.
            3. A signature skill ONLY for a character of its class, and only when the story has already established its condition - the wording of the action never triggers one:
            INSPIRE (Bard): rallying others through words or art. DEADEYE (Ranger): one perfect called shot the story sets up - ordinary shooting, however skilled, is MARKSMANSHIP. BERSERK (Barbarian): reckless all-out fury the story has built to - plain fighting is MELEE or BRAWL. ZEAL (Paladin): channeling righteous conviction. SPELLWEAVE (Mage): improvised or layered spellwork. BACKSTAB (Rogue): a strike from stealth or total surprise the story has set up - never in open combat. HEX (Witch): cursing a target. BLESSING (Cleric): invoking divine favor. COMMUNE (Druid): speaking with nature or spirits.
            For any other character, resolve the same action through skills and attributes - or IMPOSSIBLE when their nature cannot attempt it at all.
            When both a pool skill and a signature could fit, the pool skill wins unless the signature's condition is explicitly established in the story.
            Families that are often confused - pick by method or effect, never by goal:
            - Combat: armed close fighting is MELEE, shooting and throwing is MARKSMANSHIP, unarmed is BRAWL - the weapon defines the skill, the wording never does.
            - Social: telling the truth is PERSUASION, lying is DECEPTION, threatening is INTIMIDATION, entertaining is PERFORMANCE - whatever the speaker wants to achieve.
            - Magic: harming is DESTRUCTION, healing is RESTORATION, deceiving the senses is ILLUSION, creating or summoning is CONJURATION, transforming or enchanting is ALTERATION - pick by the spell's effect.
            - Movement and muscle: powering through is ATHLETICS, finesse and balance is ACROBATICS, direct application of force with no movement challenge is plain STRENGTH.
            - Knowing and noticing: LORE recalls what the character already knows, PERCEPTION notices what is present now, INTUITION reads intent and atmosphere, SURVIVAL applies wildcraft in the field.
            The same action in the same circumstances always receives the same verdict, target, and difficulty.
            A CHECK fills exactly one of skill or attribute, never both, and always a difficulty.
            An IMPOSSIBLE names the skill or attribute the attempt would have used, exactly as a check would, and difficulty is null.
            For NO_CHECK, attribute, skill, and difficulty are null.

            Difficulty is a category, never a number:
            - EASY: anyone succeeds most of the time.
            - MEDIUM: anyone with luck; the skilled reliably.
            - HARD: the skilled; the unskilled need a great roll.
            - VERY_HARD: a fresh specialist on a near-perfect roll; veterans reliably.
            - FORMIDABLE: veterans only, and not reliably.
            Difficulty reflects the task's ambition for that kind of character, never their training level - modifiers and dice settle apprentice versus master.
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
            "I brew an antidote from the marsh herbs we gathered" -> CHECK, ALCHEMY, MEDIUM: brewing is Alchemy.
            "I leap the chasm with the relic strapped to my back" -> CHECK, ACROBATICS, VERY_HARD: extreme physical feat.
            "I hold the falling portcullis up while the others crawl under" -> CHECK, STRENGTH, HARD: raw might with lives at stake, no skill fits.
            "I strike at the bandit with my sword as we circle each other" -> CHECK, MELEE, MEDIUM: armed close combat against a ready foe.
            "I loose an arrow at the fleeing thief" -> CHECK, MARKSMANSHIP, MEDIUM: a moving target at range.
            "I punch the drunkard who grabbed my cloak" -> CHECK, BRAWL, EASY: unarmed scuffle with little at stake.
            "I lift the castle gate barehanded and hurl it into the sea" (an ordinary human) -> IMPOSSIBLE: beyond their nature.
            "I flap my arms and fly over the city walls" (no wings, no magic) -> IMPOSSIBLE: physics forbids it, no roll can help.
            "I leap across the valley in a single bound" (an ordinary human) -> IMPOSSIBLE: no mortal leaps that far.
            "I leap across the valley in a single bound" (a superhuman leaper) -> CHECK, ATHLETICS, MEDIUM: within their nature, still a feat.
            "I make the door disappear" (a mage) -> CHECK, ALTERATION, VERY_HARD: ambitious magic within a mage's nature, whatever their level.
            "I make the door disappear" (no magic in their nature) -> IMPOSSIBLE: nothing about them can do this.
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
