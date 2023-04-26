package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.AIModelConverter;
import es.thalesalv.chatrpg.application.util.dbutils.StringListConverter;
import es.thalesalv.chatrpg.application.util.dbutils.StringMapDoubleConverter;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "model_settings")
public class ModelSettingsEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "name")
    private String name;

    @Column(name = "model_name", nullable = false)
    @Convert(converter = AIModelConverter.class)
    private AIModel modelName;

    @Column(name = "max_tokens", nullable = false)
    private int maxTokens;

    @Column(name = "chat_history_memory", nullable = false)
    private int chatHistoryMemory;

    @Column(name = "temperature", nullable = false)
    private double temperature;

    @Column(name = "frequency_penalty")
    private double frequencyPenalty;

    @Column(name = "presence_penalty")
    private double presencePenalty;

    @Column(name = "stop_sequence")
    @Convert(converter = StringListConverter.class)
    private List<String> stopSequence;

    @Column(name = "logit_bias")
    @Convert(converter = StringMapDoubleConverter.class)
    private Map<String, Double> logitBias;
}
