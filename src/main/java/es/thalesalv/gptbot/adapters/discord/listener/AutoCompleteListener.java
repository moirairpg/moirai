package es.thalesalv.gptbot.adapters.discord.listener;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

@Service
public class AutoCompleteListener extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {

        final String eventName = event.getName();
        final String focusedOption = event.getFocusedOption().getName();
        if (eventName.equals("lorebook") && focusedOption.equals("action")) {

            final String[] loreBookOptions = { "create", "retrieve", "update", "delete" };
            List<Command.Choice> options = Stream.of(loreBookOptions)
                    .filter(opt -> opt.startsWith(event.getFocusedOption().getValue()))
                    .map(opt -> new Command.Choice(opt, opt))
                    .collect(Collectors.toList());

            event.replyChoices(options).queue();
        }
    }
}
