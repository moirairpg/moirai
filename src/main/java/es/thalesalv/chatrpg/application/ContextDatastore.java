package es.thalesalv.chatrpg.application;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;

@Component
public class ContextDatastore {

    private ThreadLocal<EventData> commandEventData = new ThreadLocal<>();

    public void setEventData(final EventData commandEventData) {

        this.commandEventData.set(commandEventData);
    }

    public EventData getEventData() {

        return Optional.ofNullable(this.commandEventData)
        		.map(ThreadLocal::get)
        		.orElseThrow(()-> new NullPointerException("commandEventData not set on thread"));
    }

    public boolean isEventDataNull() {

        return Objects.isNull(commandEventData.get());
    }

    public void clearContext() {

        this.commandEventData.remove();
    }
}
