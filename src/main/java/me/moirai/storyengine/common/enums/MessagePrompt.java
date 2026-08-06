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
            """);

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
