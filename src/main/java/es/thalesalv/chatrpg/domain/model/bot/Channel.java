package es.thalesalv.chatrpg.domain.model.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel {

    private String id;
    private ChannelConfig channelConfig;
}