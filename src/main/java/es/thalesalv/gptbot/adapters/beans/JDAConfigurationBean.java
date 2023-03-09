package es.thalesalv.gptbot.adapters.beans;

import es.thalesalv.gptbot.adapters.discord.EventDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@RequiredArgsConstructor
public class JDAConfigurationBean {

    @Value("${config.discord.api-token}")
    private String discordApiToken;

    private final EventDispatcher dispatcherListenerAdapter;

    @Bean
    public JDA jda() {

        final Object[] eventListeners = {
                dispatcherListenerAdapter
        };

        return JDABuilder.createDefault(discordApiToken)
                .addEventListeners(eventListeners)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .build();
    }
}
