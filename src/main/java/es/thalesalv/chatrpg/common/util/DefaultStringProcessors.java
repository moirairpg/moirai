package es.thalesalv.chatrpg.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import discord4j.discordjson.json.MessageData;

public class DefaultStringProcessors {

    public static UnaryOperator<String> stripAsNamePrefixForUppercase(String name) {

        return input -> Pattern.compile("\\bAs " + name + ", (\\w)")
                .matcher(input)
                .replaceAll(r -> r.group(1)
                        .toUpperCase());
    }

    public static UnaryOperator<String> stripAsNamePrefixForLowercase(String name) {

        return input -> Pattern.compile("\\bas " + name + ", (\\w)")
                .matcher(input)
                .replaceAll(r -> r.group(1));
    }

    public static UnaryOperator<String> stripTrailingFragment() {

        return input -> Pattern.compile("(?<=[.!?\\n])\"?[^.!?\\n]*(?![.!?\\n])$", Pattern.DOTALL & Pattern.MULTILINE)
                .matcher(input)
                .replaceAll(StringUtils.EMPTY);
    }

    public static UnaryOperator<String> stripChatPrefix() {

        return input -> Pattern.compile("^[a-zA-ZÀ-úÀ-ÿ\\s]+ said:")
                .matcher(input)
                .replaceAll(StringUtils.EMPTY)
                .trim();
    }

    public static UnaryOperator<String> replacePersonaNamePlaceholderWith(String personaName) {

        return input -> Pattern.compile("\\{name\\}")
                .matcher(input)
                .replaceAll(r -> personaName);
    }

    public static UnaryOperator<String> replaceBotNameWithPersonaName(String personaName, String botName) {

        return input -> Pattern.compile(botName)
                .matcher(input)
                .replaceAll(r -> personaName);
    }

    public static UnaryOperator<String> replace(String searchRegex, String replace) {

        return input -> Pattern.compile("\\b" + searchRegex + "\\b")
                .matcher(input)
                .replaceAll(r -> replace);
    }

    public static Function<MessageData, String> formatChatMessage() {

        return message -> String.format("%s said: %s",
                message.author().username(), message.content().trim());
    }

    public static Function<String, String> formatChatMessage(String nickname) {

        return message -> String.format("%s said: %s",
                nickname, message.trim());
    }

    public static Function<MessageData, String> chatDirectiveFormatter() {

        return message -> String.format("%s said: [ %s ]",
                message.author().username(), message.content().trim());
    }

    public static Function<MessageData, String> formatQuote() {

        return originalMessage -> String.format("%s said earlier: %s",
                originalMessage.referencedMessage().get().get().author().username(),
                originalMessage.referencedMessage().get().get().content());
    }

    public static Function<MessageData, String> formatResponseToQuote() {

        return originalMessage -> String.format("%s quoted the message from %s and replied with: %s",
                originalMessage.author().username(),
                originalMessage.referencedMessage().get().get().author().username(),
                originalMessage.content());
    }

    public static Function<String, List<String>> extractDiscordIds() {

        return text -> {
            Matcher matcher = Pattern.compile("(?<=<@)\\d+(?=>)").matcher(text);

            return matcher.results()
                    .map(MatchResult::group)
                    .toList();
        };
    }
}