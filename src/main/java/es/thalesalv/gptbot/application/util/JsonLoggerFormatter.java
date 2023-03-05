package es.thalesalv.gptbot.application.util;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

public class JsonLoggerFormatter extends JsonLayout {

    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {

        Stream.of(event.getArgumentArray())
                .filter(arg -> !(arg instanceof String))
                .forEach(arg -> {
                    map.put(arg.getClass().getSimpleName(), arg);
                });
    }
}