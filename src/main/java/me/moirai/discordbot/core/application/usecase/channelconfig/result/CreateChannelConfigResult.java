package me.moirai.discordbot.core.application.usecase.channelconfig.result;

public final class CreateChannelConfigResult {

    private final String id;

    private CreateChannelConfigResult(String id) {
        this.id = id;
    }

    public static CreateChannelConfigResult build(String id) {

        return new CreateChannelConfigResult(id);
    }

    public String getId() {
        return id;
    }
}
