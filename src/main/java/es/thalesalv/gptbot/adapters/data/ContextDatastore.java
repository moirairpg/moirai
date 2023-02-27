package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.ChannelConfig;

@Component
public class ContextDatastore {

    public ThreadLocal<ChannelConfig> currentChannel = new ThreadLocal<>();

    public void setCurrentChannel(final ChannelConfig currentChannel) {
        this.currentChannel.set(currentChannel);
    }

    public ChannelConfig getCurrentChannel() {
        return this.currentChannel.get();
    }

    public boolean isCurrentChannel() {
        return !Objects.isNull(currentChannel.get());
    }

    public void clearContext() {
        this.currentChannel.remove();
    }
}
