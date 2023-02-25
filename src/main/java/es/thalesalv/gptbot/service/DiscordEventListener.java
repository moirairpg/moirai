package es.thalesalv.gptbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Service
public class DiscordEventListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordEventListener.class);

    @Override
    public void onEvent(GenericEvent event) {

        var bot = event.getJDA().getSelfUser();
        if (event instanceof ReadyEvent)
            LOGGER.info(bot.getName() + " is ready to chat!");
    }
}
