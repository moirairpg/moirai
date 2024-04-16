package es.thalesalv.chatrpg.infrastructure.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import es.thalesalv.chatrpg.infrastructure.inbound.discord.listener.DiscordEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class Discord4JConfig {

    private static final String REGISTERED_EVENT_LISTENERS = "{} discord event listeners have been registered";

    @Value("${chatrpg.discord.api.token}")
    private String discordApiToken;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<DiscordEventListener<T>> eventListeners) {

        GatewayDiscordClient client = DiscordClientBuilder.create(discordApiToken)
                .build()
                .login()
                .block();

        for (DiscordEventListener<T> listener : eventListeners) {
            client.on(listener.getEventType())
                    .flatMap(listener::onEvent)
                    .onErrorResume(listener::handleError)
                    .subscribe();
        }

        log.info(REGISTERED_EVENT_LISTENERS, eventListeners.size());

        return client;
    }
}