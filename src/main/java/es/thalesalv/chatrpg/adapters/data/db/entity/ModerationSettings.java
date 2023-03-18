package es.thalesalv.chatrpg.adapters.data.db.entity;

import java.util.Map;

import es.thalesalv.chatrpg.application.util.dbconverters.ThresholdConverter;
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
@Table(name = "moderation_settings")
public class ModerationSettings {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "is_absolute_moderation")
    private boolean isAbsolute;

    @Column(name = "thresholds", nullable = false)
    @Convert(converter = ThresholdConverter.class)
    private Map<String, Double> thresholds;
}
