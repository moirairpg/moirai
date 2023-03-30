package es.thalesalv.chatrpg.application.util;

import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.function.Function;
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

    public static UnaryOperator<String> replacePlaceholderWithPersona(Persona persona) {

        return s -> Pattern.compile("\\b\\{0\\}\\b")
                .matcher(s)
                .replaceAll(r -> persona.getName());
    }

    public static UnaryOperator<String> replaceRegex(String searchRegex, String replace) {

        return s -> Pattern.compile("\\b" + searchRegex + "\\b")
                .matcher(s)
                .replaceAll(r -> replace);
    }

    public static Function<Message, String> chatFormatter() {

        return m -> MessageFormat.format("{0} said: {1}", m.getAuthor()
                .getName(),
                m.getContentDisplay()
                        .trim());
    }

    public static Function<Message, String> chatDirectiveFormatter() {

        return m -> MessageFormat.format("{0} said: [ {1} ]", m.getAuthor()
                .getName(),
                m.getContentDisplay()
                        .trim());
    }

    public static Function<Message, String> formattedReference() {

        return m -> MessageFormat.format("{0} said earlier: {1}", m.getReferencedMessage()
                .getAuthor()
                .getName(),
                m.getReferencedMessage()
                        .getContentDisplay());
    }

    public static Function<Message, String> formattedResponse() {

        return m -> MessageFormat.format("{0} quoted the message from {1} and replied with: {2}", m.getAuthor()
                .getName(),
                m.getReferencedMessage()
                        .getAuthor()
                        .getName(),
                m.getContentDisplay());
    }
}
