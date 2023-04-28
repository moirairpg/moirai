package es.thalesalv.chatrpg.adapters.data.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lorebook_entry_regex")
public class LorebookEntryRegexEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;

    @Column(name = "regex", nullable = false)
    private String regex;

    @OneToOne
    @JoinColumn(name = "lorebook_entry_id", referencedColumnName = "id", nullable = false)
    private LorebookEntryEntity lorebookEntry;

    @ManyToOne
    @JoinColumn(name = "lorebook_id", referencedColumnName = "id", nullable = false)
    private LorebookEntity lorebook;
}
