package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.ChannelConfig;

@Component
public class ContextDatastore {

    public ThreadLocal<ChannelConfig> currentChannel = new ThreadLocal<>();

    public void setCurrentChannel(final ChannelConfig ChannelConfig) {
        this.currentChannel.set(ChannelConfig);
    }

    public ChannelConfig getCurrentChannel() {
        return this.currentChannel.get();
    }

    public void cleanCurrentChannel() {
        this.currentChannel.remove();
    }

    public boolean isCurrentChannel() {
        return !Objects.isNull(currentChannel.get());
    }
}
