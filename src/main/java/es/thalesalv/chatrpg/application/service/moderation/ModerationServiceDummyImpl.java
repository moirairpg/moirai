package es.thalesalv.chatrpg.application.service.moderation;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import reactor.core.publisher.Mono;

@Service
@Profile("no-moderation")
public class ModerationServiceDummyImpl implements ModerationService {

    @Override
    public Mono<ModerationResponse> moderate(String content, EventData eventData, ModalInteractionEvent event) {

        return Mono.just(ModerationResponse.builder().build());
    }

    @Override
    public Mono<ModerationResponse> moderate(List<String> messages, EventData eventData) {

        return Mono.just(ModerationResponse.builder().build());
    }

    @Override
    public Mono<ModerationResponse> moderateOutput(String output, EventData eventData) {

        return Mono.just(ModerationResponse.builder().build());
    }
}
