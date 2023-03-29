package es.thalesalv.chatrpg.application.service.moderation;

import java.util.List;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import reactor.core.publisher.Mono;

public interface ModerationService {

    // TODO refactor moderation methods and classes for better readability and
    // flexibility
    public Mono<ModerationResponse> moderate(final String content, final EventData eventData,
            final ModalInteractionEvent event);

    public Mono<ModerationResponse> moderate(final List<String> messages, final EventData eventData);

    public Mono<ModerationResponse> moderateOutput(final String output, final EventData eventData);
}
