package es.thalesalv.chatrpg.application.service.commands;

import es.thalesalv.chatrpg.application.service.commands.channel.ChannelConfigInteractionHandler;
import es.thalesalv.chatrpg.application.service.commands.lorebook.LorebookInteractionHandler;
import es.thalesalv.chatrpg.application.service.commands.world.WorldInteractionHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class BotCommands {

    private final ChannelConfigInteractionHandler channelConfigHandler;
    private final LorebookInteractionHandler lorebookHandler;
    private final WorldInteractionHandler worldHandler;
    private final EditInteractionHandler editHandler;

    private final PromptInteractionHandler promptService;
    private final RetryInteractionHandler retryService;
    private final StartInteractionHandler startService;
    private final TkInteractionHandler tokenizerService;

    public List<DiscordInteractionHandler> commands() {

        return Stream
                .of(channelConfigHandler, lorebookHandler, worldHandler, editHandler, promptService, retryService,
                        startService, tokenizerService)
                .collect(Collectors.<DiscordInteractionHandler>toList());
    }

    public List<SlashCommandData> list() {

        return Stream
                .of(channelConfigHandler, lorebookHandler, worldHandler, editHandler, promptService, retryService,
                        startService, tokenizerService)
                .map(DiscordInteractionHandler::buildCommand)
                .collect(Collectors.toList());
    }

    public List<SlashCommandData> listWith(DiscordInteractionHandler... data) {

        List<SlashCommandData> core = list();
        core.addAll(Stream.of(data)
                .map(DiscordInteractionHandler::buildCommand)
                .toList());
        return core;
    }

    public Optional<DiscordInteractionHandler> byName(String name) {

        return commands().stream()
                .filter(cmd -> cmd.getName()
                        .equals(name))
                .findAny();
    }
}
