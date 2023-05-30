package es.thalesalv.chatrpg.application.helper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;

@Component
public class ChatMessageFormatHelper implements MessageHelper<ChatMessage>, PersonaHelper<ChatMessage> {

    @Override
    public List<ChatMessage> formatMessages(final List<String> messages, final EventData eventData,
            final StringProcessor inputProcessor) {

        final Persona persona = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getPersona();

        final String personality = inputProcessor.process(persona.getPersonality());
        List<ChatMessage> chatMessages = messages.stream()
                .filter(msg -> !msg.trim()
                        .equals((persona.getName() + SAID).trim()))
                .map(msg -> inputProcessor.process(msg))
                .map(msg -> ChatMessage.builder()
                        .role(determineRole(msg, persona))
                        .content(formatBotName(msg, persona))
                        .build())
                .collect(Collectors.toList());

        chatMessages.add(0, ChatMessage.builder()
                .role(ROLE_SYSTEM)
                .content(personality)
                .build());

        chatMessages = formatNudge(persona, chatMessages, inputProcessor);
        chatMessages = formatBump(persona, chatMessages, inputProcessor);
        return chatMessages;
    }

    @Override
    public List<ChatMessage> formatNudge(Persona persona, List<ChatMessage> messages, StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .map(ndge -> {
                    messages.add(messages.stream()
                            .filter(m -> "user".equals(m.getRole()))
                            .mapToInt(messages::indexOf)
                            .reduce((a, b) -> b)
                            .orElse(0) + 1, ChatMessage.builder()
                                    .role(ndge.role)
                                    .content(inputProcessor.process(ndge.content))
                                    .build());
                    return messages;
                })
                .orElse(messages);
    }

    @Override
    public List<ChatMessage> formatBump(Persona persona, List<ChatMessage> messages, StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .map(bmp -> {
                    ChatMessage bumpMessage = ChatMessage.builder()
                            .role(bmp.role)
                            .content(inputProcessor.process(bmp.content))
                            .build();
                    for (int index = messages.size() - 1 - bmp.frequency; index > 0; index = index - bmp.frequency) {
                        messages.add(index, bumpMessage);
                    }
                    return messages;
                })
                .orElse(messages);
    }
}
