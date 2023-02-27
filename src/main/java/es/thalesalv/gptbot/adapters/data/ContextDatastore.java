package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.domain.model.bot.ChannelSettings;

@Component
public class ContextDatastore {

    public ThreadLocal<ChannelSettings> currentChannel = new ThreadLocal<>();

    public void setCurrentChannel(ChannelSettings channelSettings) {
        this.currentChannel.set(channelSettings);
    }

    public ChannelSettings getCurrentChannel() {
        return this.currentChannel.get();
    }

    public void cleanCurrentChannel() {
        this.currentChannel.remove();
    }

    public boolean isCurrentChannel() {
        return !Objects.isNull(currentChannel.get());
    }
}
