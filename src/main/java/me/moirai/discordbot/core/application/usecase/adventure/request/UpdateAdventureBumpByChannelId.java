package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureBumpByChannelId extends UseCase<Void> {

    private final String bump;
    private final int bumpFrequency;
    private final String channelId;

    private UpdateAdventureBumpByChannelId(Builder builder) {
        this.bump = builder.bump;
        this.bumpFrequency = builder.bumpFrequency;
        this.channelId = builder.channelId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBump() {
        return bump;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getBumpFrequency() {
        return bumpFrequency;
    }

    public static final class Builder {

        private String bump;
        private int bumpFrequency;
        private String channelId;

        public Builder bump(String bump) {
            this.bump = bump;
            return this;
        }

        public Builder bumpFrequency(int bumpFrequency) {
            this.bumpFrequency = bumpFrequency;
            return this;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public UpdateAdventureBumpByChannelId build() {
            return new UpdateAdventureBumpByChannelId(this);
        }
    }
}