package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class CreateChannelConfigFixture {

    public static CreateChannelConfig.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return CreateChannelConfig.builder()
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .moderation("strict")
                .creatorDiscordId(channelConfig.getOwnerDiscordId())
                .readerUsers(channelConfig.getReaderUsers())
                .writerUsers(channelConfig.getWriterUsers())
                .temperature(1.7)
                .visibility("private");
    }
}
