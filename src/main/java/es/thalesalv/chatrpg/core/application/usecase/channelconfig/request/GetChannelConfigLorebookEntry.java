package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

public final class GetChannelConfigLorebookEntry {

    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;

    private GetChannelConfigLorebookEntry(Builder builder) {
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public static final class Builder {

        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder playerDiscordId(String playerDiscordId) {
            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public GetChannelConfigLorebookEntry build() {
            return new GetChannelConfigLorebookEntry(this);
        }
    }
}