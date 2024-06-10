package es.thalesalv.chatrpg.infrastructure.inbound.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface DiscordEventListener<T extends Event> {

    Logger LOGGER = LoggerFactory.getLogger(DiscordEventListener.class);

    Class<T> eventType();

    Mono<Void> onEvent(T event);
}