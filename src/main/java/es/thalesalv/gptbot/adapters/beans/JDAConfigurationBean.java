package es.thalesalv.gptbot.adapters.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.thalesalv.gptbot.adapters.discord.listener.AutoCompleteListener;
import es.thalesalv.gptbot.adapters.discord.listener.GenericEventListener;
import es.thalesalv.gptbot.adapters.discord.listener.MessageListener;
import es.thalesalv.gptbot.adapters.discord.listener.SlashCommandListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
@RequiredArgsConstructor
public class JDAConfigurationBean {

    @Value("${config.discord.api-token}")
    private String discordApiToken;

    private final SlashCommandListener slashCommandListener;
    private final GenericEventListener discordBotEventListener;
    private final MessageListener discordBotMessageListener;
    private final AutoCompleteListener autoCompleteListener;

    @Bean
    public JDA jda() {

        final Object[] eventListeners = {
            discordBotEventListener, discordBotMessageListener,
            slashCommandListener, autoCompleteListener
        };

        return JDABuilder.createDefault(discordApiToken)
                .addEventListeners(eventListeners)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .build();
    }
}
