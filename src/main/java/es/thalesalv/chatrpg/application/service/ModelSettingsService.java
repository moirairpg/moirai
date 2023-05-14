package es.thalesalv.chatrpg.application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ModelSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class ModelSettingsService {

    private final JDA jda;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModelSettingsEntityToDTO modelSettingsEntityToDTO;
    private final ModelSettingsRepository modelSettingsRepository;

    private static final String DEFAULT_ID = "0";
    private static final String SETTING_ID_NOT_FOUND = "model setting with id SETTING_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSettingsService.class);

    public List<ModelSettings> retrieveAllModelSettings() {

        LOGGER.debug("Retrieving model settings data from request");
        return modelSettingsRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(modelSettingsEntityToDTO)
                .toList();
    }

    public ModelSettings retrieveModelSettingsById(final String modelSettingsId) {

        LOGGER.debug("Retrieving model settings by ID data from request");
        return modelSettingsRepository.findById(modelSettingsId)
                .map(modelSettingsEntityToDTO)
                .orElseThrow(() -> new ModelSettingsNotFoundException("Error retrieving model setting by id: "
                        + SETTING_ID_NOT_FOUND.replace("SETTING_ID", modelSettingsId)));
    }

    public ModelSettings saveModelSettings(final ModelSettings modelSettings) {

        LOGGER.debug("Saving model settings data from request");
        return Optional.of(modelSettingsDTOToEntity.apply(modelSettings))
                .map(c -> {
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    return c;
                })
                .map(c -> {
                    final var ab = modelSettingsRepository.save(c);
                    return ab;
                })
                .map(c -> {
                    final var ab = modelSettingsEntityToDTO.apply(c);
                    return ab;
                })
                .orElseThrow(() -> new RuntimeException("Error saving model setting"));
    }

    public ModelSettings updateModelSettings(final String modelSettingsId, final ModelSettings modelSettings) {

        LOGGER.debug("Updating model settings data from request");
        return Optional.of(modelSettingsDTOToEntity.apply(modelSettings))
                .map(c -> {
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    c.setId(modelSettingsId);
                    return modelSettingsRepository.save(c);
                })
                .map(modelSettingsEntityToDTO)
                .orElseThrow(() -> new ModelSettingsNotFoundException("Error updating model setting: "
                        + SETTING_ID_NOT_FOUND.replace("SETTING_ID", modelSettingsId)));
    }

    public void deleteModelSettings(final String modelSettingsId) {

        LOGGER.debug("Deleting model settings data from request");
        modelSettingsRepository.deleteById(modelSettingsId);
    }
}
