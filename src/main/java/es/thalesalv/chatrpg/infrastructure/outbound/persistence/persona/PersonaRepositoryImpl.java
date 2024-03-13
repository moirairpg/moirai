package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonas;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.persona.Bump;
import es.thalesalv.chatrpg.core.domain.persona.Nudge;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PersonaRepositoryImpl implements PersonaRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "name";

    private final PersonaJpaRepository jpaRepository;

    @Override
    public Persona save(Persona persona) {

        PersonaEntity entity = mapToEntity(persona);

        return mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<Persona> findById(String id) {

        return jpaRepository.findById(id)
                .map(this::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchPersonasResult searchPersonas(SearchPersonas query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<PersonaEntity> filters = buildFilter(query);
        Page<PersonaEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return SearchPersonasResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(page)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }

    private PersonaEntity mapToEntity(Persona persona) {

        BumpEntity bump = BumpEntity.builder()
                .content(persona.getBump().getContent())
                .role(persona.getBump().getRole().toString())
                .frequency(persona.getBump().getFrequency())
                .build();

        NudgeEntity nudge = NudgeEntity.builder()
                .content(persona.getNudge().getContent())
                .role(persona.getNudge().getRole().toString())
                .build();

        return PersonaEntity.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getReaderUsers())
                .usersAllowedToWrite(persona.getWriterUsers())
                .nudge(nudge)
                .bump(bump)
                .build();
    }

    private Persona mapFromEntity(PersonaEntity persona) {

        Bump bump = Bump.builder()
                .content(persona.getBump().getContent())
                .role(CompletionRole.fromString(persona.getBump().getRole()))
                .frequency(persona.getBump().getFrequency())
                .build();

        Nudge nudge = Nudge.builder()
                .content(persona.getNudge().getContent())
                .role(CompletionRole.fromString(persona.getNudge().getRole()))
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .build();

        return Persona.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(Visibility.fromString(persona.getVisibility()))
                .permissions(permissions)
                .nudge(nudge)
                .bump(bump)
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .creatorDiscordId(persona.getCreatorDiscordId())
                .build();
    }

    private GetPersonaResult mapToResult(PersonaEntity persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility())
                .readerUsers(persona.getUsersAllowedToRead())
                .writerUsers(persona.getUsersAllowedToWrite())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .build();
    }

    private Specification<PersonaEntity> buildFilter(SearchPersonas query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
