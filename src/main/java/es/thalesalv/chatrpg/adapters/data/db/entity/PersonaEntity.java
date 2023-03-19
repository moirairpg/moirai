package es.thalesalv.chatrpg.adapters.data.db.entity;

import es.thalesalv.chatrpg.application.util.dbconverters.BumpConverter;
import es.thalesalv.chatrpg.application.util.dbconverters.NudgeConverter;
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
@Table(name = "persona")
public class PersonaEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "intent")
    private String intent;

    @Column(name = "personality")
    private String personality;

    @Column(name = "owner_discord_id")
    private String owner;

    @Column(name = "nudge")
    @Convert(converter = NudgeConverter.class)
    private NudgeEntity nudge;

    @Column(name = "bump")
    @Convert(converter = BumpConverter.class)
    private BumpEntity bump;
}