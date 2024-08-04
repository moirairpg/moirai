package me.moirai.discordbot.core.application.usecase.discord.contextmenu;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;

@UseCaseHandler
public class EditMessageHandler extends AbstractUseCaseHandler<EditMessage, Void> {

    private final DiscordChannelPort discordChannelPort;

    public EditMessageHandler(DiscordChannelPort discordChannelPort) {
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Void execute(EditMessage useCase) {

        discordChannelPort.editMessageById(useCase.getChannelId(), useCase.getMessageId(), useCase.getMessageContent());

        return null;
    }
}
