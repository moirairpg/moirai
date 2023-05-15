package es.thalesalv.chatrpg.application.service.moderation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.domain.model.EventData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("!test-moderation")
public class ModerationFeedbackDummyService implements ModerationFeedbackService {

    @Override
    public void sendModerationFeedback(EventData eventData) {

    }
}
