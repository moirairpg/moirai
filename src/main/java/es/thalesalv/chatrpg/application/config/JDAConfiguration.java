package es.thalesalv.chatrpg.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.thalesalv.chatrpg.adapters.discord.EventDispatcher;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@RequiredArgsConstructor
public class JDAConfiguration {

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    private final EventDispatcher dispatcherListenerAdapter;

    @Bean
    public JDA jda() {

        return JDABuilder.createDefault(discordApiToken)
                .addEventListeners(dispatcherListenerAdapter)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .build();
    }
}
