package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.dbutil.StringListConverter;
import es.thalesalv.chatrpg.common.dbutil.StringMapDoubleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder(builderClassName = "Builder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModelConfigurationEntity {

    @Column(name = "ai_model", nullable = false)
    private String aiModel;

    @Column(name = "max_token_limit", nullable = false)
    private int maxTokenLimit;

    @Column(name = "message_history_size", nullable = false)
    private int messageHistorySize;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "frequency_penalty", nullable = false)
    private Double frequencyPenalty;

    @Column(name = "presence_penalty", nullable = false)
    private Double presencePenalty;

    @Column(name = "stop_sequences", nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> stopSequences;

    @Column(name = "logit_bias", nullable = false)
    @Convert(converter = StringMapDoubleConverter.class)
    private Map<String, Double> logitBias;
}
