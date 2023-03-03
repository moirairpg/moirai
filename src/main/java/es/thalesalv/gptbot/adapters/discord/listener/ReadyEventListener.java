package es.thalesalv.gptbot.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Service
public class ReadyEventListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyEventListener.class);

    @Override
    public void onEvent(GenericEvent event) {

        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            if (event instanceof ReadyEvent)
                LOGGER.info("{} is ready to chat!", bot.getName());
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Session is not yet ready!")) {
                LOGGER.warn("Waiting for Discord session...");
            } else {
                LOGGER.error("Error during event", e);
            }
        }
    }
}
