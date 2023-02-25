package es.thalesalv.gptbot.data;

import es.thalesalv.gptbot.model.ChannelSettings;
import org.springframework.stereotype.Component;

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
}
