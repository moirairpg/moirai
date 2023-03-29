package es.thalesalv.chatrpg.domain.model.openai;

import es.thalesalv.chatrpg.domain.enums.Source;
import es.thalesalv.chatrpg.domain.model.PromptPartObj;
import net.dv8tion.jda.api.entities.Message;

import java.util.Optional;

public class MessageToPromptPartMapper {

    public PromptPartObj map(Message message) {

        return PromptPartObj.builder()
                .content(message.getContentDisplay())
                .source(Source.MESSAGE)
                .timeCreated(Optional.of(message.getTimeCreated()))
                .build();
    }
}
