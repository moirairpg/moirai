package me.moirai.discordbot.infrastructure.inbound.discord.slashcommands;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.interactions.commands.OptionType;

@Component
public class TokenizeSlashCommand extends DiscordSlashCommand {

    @Override
    public String getName() {
        return "tokenize";
    }

    @Override
    public String getDescription() {
        return "Tokenizes given input";
    }

    @Override
    public List<DiscordSlashCommandOption> getOptions() {

        List<DiscordSlashCommandOption> options = new ArrayList<>();

        options.add(DiscordSlashCommandOption
                .build("input", "Input text to be tokenized", OptionType.STRING));

        return options;
    }
}
