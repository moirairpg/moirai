package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.RetryGeneration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private final UseCaseRunner useCaseRunner;

    public SlashCommandListener(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        event.deferReply();

        TextChannel textChannel = event.getChannel().asTextChannel();
        Guild guild = event.getGuild();
        Member author = guild.retrieveMember(event.getUser()).complete();
        Member bot = guild.retrieveMember(event.getJDA().getSelfUser()).complete();

        String command = event.getFullCommandName();
        switch (command) {
            case "retry" -> {
                InteractionHook interactionHook = event.reply("Generating new output...").setEphemeral(true).complete();

                RetryGeneration useCase = RetryGeneration.builder()
                        .botId(bot.getId())
                        .botNickname(bot.getNickname())
                        .botUsername(bot.getUser().getName())
                        .guildId(guild.getId())
                        .channelId(textChannel.getId())
                        .build();

                useCaseRunner.run(useCase)
                        .doOnError(error -> editInteractionEphemeralMessage(interactionHook, error.getMessage()))
                        .subscribe(output -> editInteractionEphemeralMessage(interactionHook, output));
            }
        }
    }

    private Message editInteractionEphemeralMessage(InteractionHook interactionHook, String newContent) {
        return interactionHook.editOriginal(newContent).complete();
    }
}
