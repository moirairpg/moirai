package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.data.ContextDatastore;
import es.thalesalv.gptbot.data.db.CharacterProfileRepository;
import es.thalesalv.gptbot.service.GptService;
import es.thalesalv.gptbot.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class RPGUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    private final CharacterProfileRepository characterProfileRepository;

    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";
    private static final String CHARACTER_DESCRIPTION = "{1}''s description is: {2}";
    private static final Logger LOGGER = LoggerFactory.getLogger(RPGUseCase.class);
    
    public void generateResponse(SelfUser bot, User player, Mentions mentions, MessageChannelUnion channel) {
        
        LOGGER.debug("Entered generation of response for RPG");

        var messages = new ArrayList<String>();
        channel.getHistory()
            .retrievePast(contextDatastore.getCurrentChannel().getChatHistoryMemory()).complete()
            .forEach(m -> {
                messages.add(MessageFormat.format("{0} said: {1}",
                    m.getAuthor().getName(), m.getContentDisplay().trim()));
            });
        
        Collections.reverse(messages);

        var characterProfile = characterProfileRepository.findByPlayerDiscordId(player.getId());
        if (characterProfile != null) {
            messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, characterProfile.getName()));
            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, player.getAsTag(),
                    characterProfile.getName(), characterProfile.getDescription()));

            messages.replaceAll(message -> message.replaceAll(player.getAsTag(), characterProfile.getName())
                    .replaceAll(player.getName(), characterProfile.getName()));
        }

        mentions.getUsers().stream()
            .forEach(mention -> {
                final var mentionedProfile = characterProfileRepository.findByPlayerDiscordId(mention.getId());
                if (mentionedProfile != null) {
                    messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, mentionedProfile.getName()));
                    messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, mention.getAsTag(),
                            mentionedProfile.getName(), mentionedProfile.getDescription()));

                    messages.replaceAll(message -> message.replaceAll(mention.getAsTag(), mentionedProfile.getName())
                            .replaceAll(mention.getName(), mentionedProfile.getName()));
                }
            });

        MessageUtils.formatPersonality(messages, contextDatastore.getCurrentChannel(), bot);
        gptService.callDaVinci(MessageUtils.chatifyMessages(bot, messages))
                .filter(r -> !r.getChoices().get(0).getText().isBlank())
                .map(response -> {
                    var responseText = response.getChoices().get(0).getText();
                    channel.sendMessage(responseText.trim()).queue();
                    return response;
                }).subscribe();
    }
}
