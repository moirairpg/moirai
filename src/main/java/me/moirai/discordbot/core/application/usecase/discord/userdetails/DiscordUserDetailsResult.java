package me.moirai.discordbot.core.application.usecase.discord.userdetails;

public class DiscordUserDetailsResult {

    private String id;
    private String username;
    private String globalNickname;
    private String avatar;

    public DiscordUserDetailsResult() {
    }

    private DiscordUserDetailsResult(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.globalNickname = builder.globalNickname;
        this.avatar = builder.avatar;
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

    public String getGlobalNickname() {
        return globalNickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public static final class Builder {
        private String id;
        private String username;
        private String globalNickname;
        private String avatar;

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

        public Builder globalNickname(String globalNickname) {
            this.globalNickname = globalNickname;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public DiscordUserDetailsResult build() {
            return new DiscordUserDetailsResult(this);
        }
    }
}