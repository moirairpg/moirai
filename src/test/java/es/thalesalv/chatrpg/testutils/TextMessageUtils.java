package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.List;

public class TextMessageUtils {

    public static List<String> createChat() {

        final List<String> messages = new ArrayList<>();
        messages.add("John said: Hello");
        messages.add("Martha said: Hello, how are you?");
        messages.add("John said: I am fine. How is the bot?");
        messages.add("ChatRPG said: As an AI language model, I cannot be well or bad. But thank you for asking.");
        messages.add("Martha says: yeah right");
        messages.add("ChatRPG said:");

        return messages;
    }
}
