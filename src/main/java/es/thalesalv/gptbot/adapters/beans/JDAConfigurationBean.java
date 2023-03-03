package es.thalesalv.gptbot.adapters.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.thalesalv.gptbot.adapters.discord.listener.ReadyEventListener;
import es.thalesalv.gptbot.adapters.discord.listener.MessageListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@RequiredArgsConstructor
public class JDAConfigurationBean {

    @Value("${config.discord.api-token}")
    private String discordApiToken;

    private final ReadyEventListener discordBotEventListener;
    private final MessageListener discordBotMessageListener;

    @Bean
    public JDA jda() {

        return JDABuilder.createDefault(discordApiToken)
                .addEventListeners(discordBotEventListener, discordBotMessageListener)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .build();
    }
}
