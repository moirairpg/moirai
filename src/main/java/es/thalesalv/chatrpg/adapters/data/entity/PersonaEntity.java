package es.thalesalv.chatrpg.adapters.data.entity;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.application.util.dbutils.BumpConverter;
import es.thalesalv.chatrpg.application.util.dbutils.NudgeConverter;
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
@Table(name = "persona")
public class PersonaEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", strategy = "es.thalesalv.chatrpg.application.util.dbutils.NanoIdIdentifierGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "intent")
    private String intent;

    @Column(name = "personality", length = 10000)
    private String personality;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "nudge", length = 10000)
    @Convert(converter = NudgeConverter.class)
    private NudgeEntity nudge;

    @Column(name = "bump", length = 10000)
    @Convert(converter = BumpConverter.class)
    private BumpEntity bump;
}