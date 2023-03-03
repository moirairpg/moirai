package es.thalesalv.gptbot.application.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.gptbot.application.config.Persona;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtils {

    public static String chatifyMessages(final User bot, final List<String> messages) {

        messages.replaceAll(message -> message.replaceAll("@" + bot.getName(), StringUtils.EMPTY).trim());
        return MessageFormat.format("{0}\n{1} said: ",
                messages.stream().collect(Collectors.joining("\n")), bot.getName()).trim();
    }

    public static void formatPersonality(List<String> messages, Persona persona, SelfUser bot) {

        messages.add(0, MessageFormat.format(persona.getPersonality(), bot.getName())
                .replace("@" + bot.getName(), StringUtils.EMPTY).trim());
    }
}
