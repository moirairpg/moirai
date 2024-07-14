package me.moirai.discordbot.infrastructure.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import me.moirai.discordbot.infrastructure.inbound.discord.listener.DiscordEventErrorHandler;
import me.moirai.discordbot.infrastructure.inbound.discord.listener.DiscordEventListener;

@Configuration
public class Discord4JConfig {

    private static final Logger LOG = LoggerFactory.getLogger(Discord4JConfig.class);

    private static final String REGISTERED_EVENT_LISTENERS = "{} discord event listeners have been registered";

    private final String discordApiToken;
    private final DiscordEventErrorHandler errorHandler;

    public Discord4JConfig(
            @Value("${moirai.discord.api.token}") String discordApiToken,
            DiscordEventErrorHandler errorHandler) {

        this.discordApiToken = discordApiToken;
        this.errorHandler = errorHandler;
    }

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<DiscordEventListener<T>> eventListeners) {

        GatewayDiscordClient client = DiscordClientBuilder.create(discordApiToken)
                .build()
                .login()
                .block();

        for (DiscordEventListener<T> listener : eventListeners) {
            client.on(listener.eventType())
                    .flatMap(listener::onEvent)
                    .doOnError(errorHandler::handle)
                    .subscribe();
        }

        LOG.info(REGISTERED_EVENT_LISTENERS, eventListeners.size());

        return client;
    }
}