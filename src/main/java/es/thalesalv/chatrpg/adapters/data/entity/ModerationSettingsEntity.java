package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.Map;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.StringMapDoubleConverter;
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
@Table(name = "moderation_settings")
public class ModerationSettingsEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "is_absolute_moderation")
    private boolean isAbsolute;

    @Column(name = "thresholds")
    @Convert(converter = StringMapDoubleConverter.class)
    private Map<String, Double> thresholds;
}
