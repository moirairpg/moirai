package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.List;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.StringListConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "channel_config")
public class ChannelConfigEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "owner_discord_id", nullable = false)
    private String owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "write_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> writePermissions;

    @Column(name = "read_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> readPermissions;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @JoinColumn(name = "persona_id", referencedColumnName = "id", nullable = false)
    private PersonaEntity persona;

    @OneToOne(cascade = CascadeType.ALL)
    @ToString.Exclude
    @JoinColumn(name = "model_settings_id", referencedColumnName = "id", nullable = false)
    private ModelSettingsEntity modelSettings;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @JoinColumn(name = "moderation_settings_id", referencedColumnName = "id", nullable = false)
    private ModerationSettingsEntity moderationSettings;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @JoinColumn(name = "world_id", referencedColumnName = "id", nullable = false)
    private WorldEntity world;

    @OneToMany(mappedBy = "channelConfig")
    private Set<ChannelEntity> channels;
}