package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.List;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.BumpConverter;
import es.thalesalv.chatrpg.application.util.dbutils.IntentConverter;
import es.thalesalv.chatrpg.application.util.dbutils.NudgeConverter;
import es.thalesalv.chatrpg.application.util.dbutils.StringListConverter;
import es.thalesalv.chatrpg.domain.enums.Intent;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "persona")
public class PersonaEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "intent", nullable = false)
    @Convert(converter = IntentConverter.class)
    private Intent intent;

    @Column(name = "personality", length = 10000, nullable = false)
    private String personality;

    @Column(name = "owner_discord_id", nullable = false)
    private String ownerDiscordId;

    @Column(name = "visibility", nullable = false)
    private String visibility;

    @Column(name = "is_multiplayer", nullable = false, columnDefinition = "boolean default false")
    private boolean isMultiplayer;

    @Column(name = "write_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> writePermissions;

    @Column(name = "read_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> readPermissions;

    @Column(name = "nudge", length = 10000)
    @Convert(converter = NudgeConverter.class)
    private NudgeEntity nudge;

    @Column(name = "bump", length = 10000)
    @Convert(converter = BumpConverter.class)
    private BumpEntity bump;

    @OneToMany(mappedBy = "persona")
    private Set<ChannelConfigEntity> channelConfigs;
}
