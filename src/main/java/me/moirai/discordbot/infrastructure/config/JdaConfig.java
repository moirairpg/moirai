package me.moirai.discordbot.infrastructure.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
public class JdaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JdaConfig.class);

    private static final String REGISTERED_EVENT_LISTENERS = "{} discord event listeners have been registered";

    private final String discordApiToken;

    public JdaConfig(@Value("${moirai.discord.api.token}") String discordApiToken) {

        this.discordApiToken = discordApiToken;
    }

    @Bean
    <T extends ListenerAdapter> JDA jda(List<T> eventListeners) {

        JDABuilder jdaBuilder = JDABuilder.createDefault(discordApiToken);

        for (T listener : eventListeners) {
            LOG.debug("Registering event listener: " + listener.getClass().getSimpleName());
            jdaBuilder.addEventListeners(listener);
        }

        LOG.info(REGISTERED_EVENT_LISTENERS, eventListeners.size());
        return jdaBuilder.setActivity(Activity.watching("Writing stories, inspiring adventures."))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .build();
    }
}
