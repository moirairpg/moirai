package es.thalesalv.chatrpg.application.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class StringProcessors {

    public static UnaryOperator<String> stripAsNamePrefixForUppercase(String name) {

        return s -> Pattern.compile("\\bAs " + name + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1)
                        .toUpperCase());
    }

    public static UnaryOperator<String> stripAsNamePrefixForLowercase(String name) {

        return s -> Pattern.compile("\\bas " + name + ", (\\w)")
                .matcher(s)
                .replaceAll(r -> r.group(1));
    }

    public static UnaryOperator<String> stripTrailingFragment() {

        return s -> Pattern.compile("(?<=[.!?\\n])\"?[^.!?\\n]*(?![.!?\\n])$", Pattern.DOTALL & Pattern.MULTILINE)
                .matcher(s)
                .replaceAll(StringUtils.EMPTY);
    }

    public static UnaryOperator<String> stripChatPrefix() {

        return s -> Pattern.compile("^\\w* said:")
                .matcher(s)
                .replaceAll(StringUtils.EMPTY);
    }
}
