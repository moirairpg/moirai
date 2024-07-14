package es.thalesalv.chatrpg.core.application.usecase.discord;

public final class DiscordUserDetails {

    private final String id;
    private final String username;
    private final String globalName;
    private final String displayName;
    private final String mention;

    public DiscordUserDetails(Builder builder) {

        this.id = builder.id;
        this.username = builder.username;
        this.globalName = builder.globalName;
        this.displayName = builder.displayName;
        this.mention = builder.mention;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getGlobalName() {
        return globalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMention() {
        return mention;
    }

    public static final class Builder {

        private String id;
        private String username;
        private String globalName;
        private String displayName;
        private String mention;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder globalName(String globalName) {
            this.globalName = globalName;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder mention(String mention) {
            this.mention = mention;
            return this;
        }

        public DiscordUserDetails build() {
            return new DiscordUserDetails(this);
        }
    }
}
