package es.thalesalv.gptbot.adapters.data.db.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "character_regex")
public class CharacterRegexEntity {

    @Id
    private UUID id;

    @Column(name = "regex")
    private String regex;

    @OneToOne
    @JoinColumn(name="character_id", referencedColumnName="id")
    private CharacterProfileEntity characterProfile;
}
