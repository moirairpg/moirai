package es.thalesalv.chatrpg.adapters.data.entity;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "lorebook")
public class LorebookEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "lorebook_name")
    private String name;

    @Column(name = "lorebook_description", length = 2000)
    private String description;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "write_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> writePermissions;

    @Column(name = "read_permission_discord_ids")
    @Convert(converter = StringListConverter.class)
    private List<String> readPermissions;

    @Column(name = "visibility")
    private String visibility;

    @OneToMany(mappedBy = "lorebook", fetch = FetchType.EAGER)
    private List<LorebookEntryRegexEntity> entries;
}