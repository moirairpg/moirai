package es.thalesalv.gptbot.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.gptbot.model.bot.ChannelSettings;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

public class MessageUtils {

    public static String chatifyMessages(User bot, List<String> messages) {

        messages.replaceAll(message -> message.replaceAll("@" + bot.getName(), StringUtils.EMPTY).trim());
        return MessageFormat.format("{0}\n{1} (ID {2}) said: ",
                messages.stream().collect(Collectors.joining("\n")), bot.getName(), bot.getId()).trim();
    }

    public static void formatPersonality(List<String> messages, ChannelSettings currentChannel, SelfUser bot) {

        messages.add(0, currentChannel.getChannelInstructions()
                .replace("<BOT.NAME>", bot.getAsTag())
                .replace("<BOT.NICK>", bot.getName())
                .replace("<personality.species>", currentChannel.getPersonality().getSpecies())
                .replace("<personality.behavior>", currentChannel.getPersonality().getBehavior())
                .replace("<personality.duties>", currentChannel.getPersonality().getBehavior())
                .replace("@" + bot.getName(), StringUtils.EMPTY).trim());
    }
}
