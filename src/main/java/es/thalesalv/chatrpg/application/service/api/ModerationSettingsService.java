package es.thalesalv.chatrpg.application.service.api;

import java.util.List;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ModerationSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModerationSettingsService {

    private final ModerationSettingsDTOToEntity moderationSettingsServiceDTOToEntity;
    private final ModerationSettingsEntityToDTO moderationSettingsServiceEntityToDTO;

    private final ModerationSettingsRepository moderationSettingsServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsService.class);

    public Mono<List<ModerationSettings>> retrieveAllModerationSettings() {

        LOGGER.debug("Retrieving moderation settings data from request");
        return Mono.just(moderationSettingsServiceRepository.findAll())
                .map(moderationSettingsServices -> moderationSettingsServices.stream()
                        .map(moderationSettingsServiceEntityToDTO)
                        .toList());
    }

    public Mono<List<ModerationSettings>> retrieveModerationSettingsById(final String moderationSettingsServiceId) {

        LOGGER.debug("Retrieving moderation settings by ID data from request");
        return Mono.just(moderationSettingsServiceRepository.findById(moderationSettingsServiceId)
                .orElseThrow(ModerationSettingsNotFoundException::new))
                .map(moderationSettingsService -> Stream.of(moderationSettingsService)
                        .map(moderationSettingsServiceEntityToDTO)
                        .toList());
    }

    public Mono<List<ModerationSettings>> saveModerationSettings(final ModerationSettings moderationSettingsService) {

        LOGGER.debug("Saving moderation settings data from request");
        return Mono.just(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(moderationSettingsServiceRepository::save)
                .map(moderationSettingsServiceEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public Mono<List<ModerationSettings>> updateModerationSettings(final String moderationSettingsServiceId,
            final ModerationSettings moderationSettingsService) {

        LOGGER.debug("Updating moderation settings data from request");
        return Mono.just(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(c -> {
                    c.setId(moderationSettingsServiceId);
                    return moderationSettingsServiceRepository.save(c);
                })
                .map(moderationSettingsServiceEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public void deleteModerationSettings(final String moderationSettingsServiceId) {

        LOGGER.debug("Deleting moderation settings data from request");
        moderationSettingsServiceRepository.deleteById(moderationSettingsServiceId);
    }
}
