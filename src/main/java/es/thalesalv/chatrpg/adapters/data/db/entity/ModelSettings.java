package es.thalesalv.chatrpg.adapters.data.db.entity;

import java.util.Map;

import es.thalesalv.chatrpg.application.util.dbconverters.LogitBiasConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
public class ModelSettings {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_family")
    private String modelFamily;

    @Column(name = "stop_token")
    private String stopToken;

    @Column(name = "max_token")
    private int maxTokens;

    @Column(name = "chat_history_memory")
    private int chatHistoryMemory;

    @Column(name = "temperature")
    private double temperature;

    @Column(name = "frequency_penalty")
    private double frequencyPenalty;

    @Column(name = "presence_penalty")
    private double presencePenalty;

    @Column(name = "logit_bias")
    @Convert(converter = LogitBiasConverter.class)
    private Map<String, Integer> logitBias;
}
