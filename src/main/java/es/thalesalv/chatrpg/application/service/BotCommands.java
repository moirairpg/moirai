package es.thalesalv.chatrpg.application.service;

import es.thalesalv.chatrpg.application.service.commands.*;
import es.thalesalv.chatrpg.application.service.commands.chconf.ChConfCommandService;
import es.thalesalv.chatrpg.application.service.commands.lorebook.LorebookCommandService;
import es.thalesalv.chatrpg.application.service.commands.world.WorldCommandService;
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

    private final ChConfCommandService chconfService;
    private final LorebookCommandService lbService;
    private final WorldCommandService worldService;
    private final EditCommandService editService;
    private final HelpCommandService helpService;

    private final PromptCommandService promptService;
    private final RetryCommandService retryService;
    private final StartCommandService startService;
    private final TkCommandService tokenizerService;
    private final UnsetCommandService unsetService;

    public List<DiscordCommand> commands() {

        return Stream
                .of(chconfService, lbService, worldService, editService, helpService, promptService, retryService,
                        startService, tokenizerService, unsetService)
                .collect(Collectors.toList());
    }

    public List<SlashCommandData> list() {

        return Stream
                .of(chconfService, lbService, worldService, editService, helpService, promptService, retryService,
                        startService, tokenizerService, unsetService)
                .map(DiscordCommand::buildCommand)
                .collect(Collectors.toList());
    }

    public List<SlashCommandData> listWith(DiscordCommand... data) {

        List<SlashCommandData> core = list();
        core.addAll(Stream.of(data)
                .map(DiscordCommand::buildCommand)
                .toList());
        return core;
    }

    public Optional<DiscordCommand> byName(String name) {

        return commands().stream()
                .filter(cmd -> cmd.getName()
                        .equals(name))
                .findAny();
    }
}
