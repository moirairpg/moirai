package es.thalesalv.chatrpg.infrastructure.persistence.jpa.lorebook;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lorebook_entry")
public class LorebookEntryJpaEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    private String id;
}
