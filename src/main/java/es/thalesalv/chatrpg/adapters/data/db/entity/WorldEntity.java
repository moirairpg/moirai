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
@Table(name = "world")
public class WorldEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "edit_permission_discord_ids")
    private String editPermissions;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "initial_prompt", length = 2000)
    private String initialPrompt;

    @OneToOne
    @JoinColumn(name = "lorebook", referencedColumnName = "id")
    private LorebookEntity lorebook;
}
