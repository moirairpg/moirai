package es.thalesalv.chatrpg.domain.model;

import net.dv8tion.jda.api.entities.Message;

public class PromptBuilder {

    public interface MessagesBuilder {
        MessagesBuilder addMessage(Message message);
    }
}
