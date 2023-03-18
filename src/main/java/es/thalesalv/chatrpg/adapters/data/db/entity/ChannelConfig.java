package es.thalesalv.chatrpg.adapters.data.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "channel_config")
public class ChannelConfig {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "owner_discord_id", nullable = false)
    private String owner;

    @Column(name = "edit_permission_discord_ids")
    private String editPermissions;

    @OneToOne
    @JoinColumn(name = "persona_id", referencedColumnName = "id", nullable = false)
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "model_settings_id", referencedColumnName = "id", nullable = false)
    private ModelSettings modelSettings;

    @OneToOne
    @JoinColumn(name = "moderation_settings_id", referencedColumnName = "id", nullable = false)
    private ModerationSettings moderationSettings;
}
