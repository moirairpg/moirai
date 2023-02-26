package es.thalesalv.gptbot.usecases;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.gptbot.data.db.CharacterProfileRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class RPGUseCase {

    private final ObjectMapper objectMapper;
    private final CharacterProfileRepository characterProfileRepository;

    private static final String CHARACTER_DESCRIPTION = "{0}'s character description: {1}\n";
    private static final Logger LOGGER = LoggerFactory.getLogger(RPGUseCase.class);
    
    public void generateResponse(List<String> messages, User player, Mentions mentions, MessageChannelUnion channel) {
        
        try {
            var characterProfile = characterProfileRepository.findByPlayerDiscordId(player.getId());
            messages.add(MessageFormat.format(CHARACTER_DESCRIPTION, player.getAsTag(), objectMapper.writeValueAsString(characterProfile)));
            mentions.getUsers().forEach(mention -> {
                try {
                    final var mentionedProfile = characterProfileRepository.findByPlayerDiscordId(mention.getId());
                    messages.add(MessageFormat.format(CHARACTER_DESCRIPTION, mention.getAsTag(), objectMapper.writeValueAsString(mentionedProfile)));
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error parsing character profile to JSON -> {}", e);
                    throw new RuntimeException(e);
                }
            });

            channel.getHistory()
                    .retrievePast(5).complete()
                    .forEach(m -> {
                        messages.add(MessageFormat.format("{0} said: {1}",
                            m.getAuthor().getAsTag(), m.getContentDisplay().trim()));
                    });
    
            Collections.reverse(messages);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing character profile to JSON -> {}", e);
            throw new RuntimeException(e);
        }
    }
}
