package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.common.usecases.UseCase;
import reactor.core.publisher.Mono;

public final class MessageReceived extends UseCase<Mono<Void>> {

    private final String authordDiscordId;
    private final String messageId;
    private final String messageChannelId;
    private final String messageGuildId;
    private final String botUsername;
    private final String botNickname;
    private final boolean isBotMentioned;
    private final List<String> mentionedUsersIds;

    public MessageReceived(Builder builder) {

        this.authordDiscordId = builder.authordDiscordId;
        this.messageId = builder.messageId;
        this.messageChannelId = builder.messageChannelId;
        this.messageGuildId = builder.messageGuildId;
        this.botUsername = builder.botUsername;
        this.botNickname = builder.botNickname;
        this.isBotMentioned = builder.isBotMentioned;
        this.mentionedUsersIds = unmodifiableList(builder.mentionedUsersIds);
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

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotNickname() {
        return botNickname;
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
        private String botUsername;
        private String botNickname;
        private boolean isBotMentioned;
        private List<String> mentionedUsersIds = new ArrayList<>();

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

        public Builder botUsername(String botUsername) {
            this.botUsername = botUsername;
            return this;
        }

        public Builder botNickname(String botNickname) {
            this.botNickname = botNickname;
            return this;
        }

        public Builder mentionedUsersIds(List<String> mentionedUsersIds) {

            if (mentionedUsersIds != null) {
                this.mentionedUsersIds = mentionedUsersIds;
            }

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
