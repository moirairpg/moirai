package es.thalesalv.chatrpg.application.helper;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

public interface MessageHelper<T> {

    static final String SAID = " said: ";
    static final String ROLE_USER = "user";
    static final String ROLE_SYSTEM = "system";
    static final String TAG_EXPRESSION = "(@|)";
    static final String BOT_SAID = "{0}\n{1} said: ";
    static final String ROLE_ASSISTANT = "assistant";
    static final String REMEMBER_TO_NEVER = "I will remember to never";
    static final String CHARACTER_DESCRIPTION = "{0} description: {1}";
    static final String CHAT_EXPRESSION = "^(.*) (says|said|quoted|replied).*";
    static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";

    static final Logger LOGGER = LoggerFactory.getLogger(MessageHelper.class);

    List<T> formatMessages(final List<String> messages, final EventData eventData,
            final StringProcessor inputProcessor);

    default String formatBotName(final String msg, final Persona persona) {

        return msg.replaceAll(persona.getName() + SAID, StringUtils.EMPTY);
    }

    default String determineRole(final String message, final Persona persona) {

        final boolean isChat = message.matches(CHAT_EXPRESSION);
        if (message.startsWith(persona.getName())) {
            return ROLE_ASSISTANT;
        } else if (isChat && !message.startsWith(REMEMBER_TO_NEVER)) {
            return ROLE_USER;
        }

        return ROLE_SYSTEM;
    }

    /**
     * Stringifies messages and turns them into a prompt format
     *
     * @param messages  Messages in the chat room
     * @param eventData Object containing event data
     * @return Stringified messages for prompt
     */
    default public String chatifyMessages(final List<String> messages, final EventData eventData,
            final StringProcessor inputProcessor) {

        LOGGER.debug("Entered chatbot conversation formatter");
        final Persona persona = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getPersona();

        messages.replaceAll(m -> m.replace(eventData.getBot()
                .getName(), persona.getName())
                .trim());

        final String promptContent = MessageFormat
                .format(BOT_SAID, String.join(StringUtils.LF, messages), persona.getName())
                .trim();

        return inputProcessor.process(promptContent);
    }

    default public String stringifyMessages(final List<String> messages) {

        return String.join(StringUtils.LF, messages)
                .trim();
    }
}