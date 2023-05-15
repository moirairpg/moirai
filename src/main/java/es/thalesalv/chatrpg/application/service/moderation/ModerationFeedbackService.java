package es.thalesalv.chatrpg.application.service.moderation;

import es.thalesalv.chatrpg.domain.model.EventData;

public interface ModerationFeedbackService {

    void sendModerationFeedback(final EventData eventData);
}
