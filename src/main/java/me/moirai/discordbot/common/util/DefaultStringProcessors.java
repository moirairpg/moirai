package me.moirai.discordbot.common.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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

        return input -> Pattern.compile("^.* said:")
                .matcher(input)
                .replaceAll(StringUtils.EMPTY)
                .trim();
    }

    public static UnaryOperator<String> replacePersonaNamePlaceholderWith(String personaName) {

        return input -> Pattern.compile("\\{name\\}")
                .matcher(input)
                .replaceAll(r -> personaName);
    }

    public static UnaryOperator<String> replaceTemplateWithValue(String newValue, String template) {

        return input -> Pattern.compile(template, Pattern.MULTILINE)
                .matcher(input)
                .replaceAll(r -> newValue);
    }

    public static UnaryOperator<String> replaceTemplateWithValueIgnoreCase(String newValue, String template) {

        return input -> Pattern.compile(template, Pattern.CASE_INSENSITIVE & Pattern.MULTILINE)
                .matcher(input)
                .replaceAll(r -> newValue);
    }

    public static Function<String, String> formatChatMessage(String nickname) {

        return message -> String.format("%s said: %s", nickname, message.trim());
    }

    public static Function<String, String> formatAuthorDirective(String nickname) {

        return message -> String.format("%s said: [ %s ]", nickname, message.trim());
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