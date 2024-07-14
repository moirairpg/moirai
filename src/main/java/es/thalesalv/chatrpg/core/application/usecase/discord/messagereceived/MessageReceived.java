package es.thalesalv.chatrpg.core.application.usecase.discord.messagereceived;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import reactor.core.publisher.Mono;

public final class MessageReceived extends UseCase<Mono<Void>> {

    private final String authordDiscordId;
    private final String messageId;
    private final String messageChannelId;
    private final String messageGuildId;
    private final String botName;
    private final List<String> mentionedUsersIds;
    private final boolean isBotMentioned;

    public MessageReceived(Builder builder) {

        this.authordDiscordId = builder.authordDiscordId;
        this.messageId = builder.messageId;
        this.messageChannelId = builder.messageChannelId;
        this.messageGuildId = builder.messageGuildId;
        this.botName = builder.botName;
        this.mentionedUsersIds = builder.mentionedUsersIds;
        this.isBotMentioned = builder.isBotMentioned;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAuthordDiscordId() {
        return authordDiscordId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageChannelId() {
        return messageChannelId;
    }

    public String getMessageGuildId() {
        return messageGuildId;
    }

    public String getBotName() {
        return botName;
    }

    public List<String> getMentionedUsersIds() {
        return mentionedUsersIds;
    }

    public boolean isBotMentioned() {
        return isBotMentioned;
    }

    public static final class Builder {

        private String authordDiscordId;
        private String messageId;
        private String messageChannelId;
        private String messageGuildId;
        private String botName;
        private List<String> mentionedUsersIds;
        private boolean isBotMentioned;

        private Builder() {
        }

        public Builder authordDiscordId(String authordDiscordId) {
            this.authordDiscordId = authordDiscordId;
            return this;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder messageChannelId(String messageChannelId) {
            this.messageChannelId = messageChannelId;
            return this;
        }

        public Builder messageGuildId(String messageGuildId) {
            this.messageGuildId = messageGuildId;
            return this;
        }

        public Builder botName(String botName) {
            this.botName = botName;
            return this;
        }

        public Builder mentionedUsersIds(List<String> mentionedUsersIds) {
            this.mentionedUsersIds = mentionedUsersIds;
            return this;
        }

        public Builder isBotMentioned(boolean isBotMentioned) {
            this.isBotMentioned = isBotMentioned;
            return this;
        }

        public MessageReceived build() {
            return new MessageReceived(this);
        }
    }
}
