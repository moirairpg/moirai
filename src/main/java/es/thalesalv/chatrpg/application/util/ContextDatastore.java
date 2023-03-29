package es.thalesalv.chatrpg.application.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.EventData;

@Component
public class ContextDatastore {

    private ThreadLocal<EventData> eventData = new ThreadLocal<>();

    public void setEventData(final EventData eventData) {

        this.eventData.set(eventData);
    }

    public EventData getEventData() {

        return Optional.ofNullable(this.eventData.get())
                .orElseThrow(() -> new NullPointerException("eventData not set on thread"));
    }

    public void clearContext() {

        this.eventData.remove();
    }
}
