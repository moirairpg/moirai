package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import org.springframework.data.jpa.repository.JpaRepository;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface PersonaJpaRepository
        extends JpaRepository<PersonaEntity, String>, PaginationRepository<PersonaEntity, String> {

}
