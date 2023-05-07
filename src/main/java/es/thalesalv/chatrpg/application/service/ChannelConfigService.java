package es.thalesalv.chatrpg.application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelConfigEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class ChannelConfigService {

    private final JDA jda;
    private final PersonaDTOToEntity personaDTOToEntity;
    private final WorldDTOToEntity worldDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ChannelConfigEntityToDTO channelConfigEntityToDTO;

    private final PersonaRepository personaRepository;
    private final WorldRepository worldRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final String CHANNEL_CONFIG_ID_NOT_FOUND = "Channel config with id CHANNEL_CONFIG_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigService.class);

    public List<ChannelConfig> retrieveAllChannelConfigs(final String userId) {

        LOGGER.debug("Entered retrieveAllChannelConfigs. userId -> {}", userId);
        return channelConfigRepository.findAll()
                .stream()
                .filter(l -> {
                    final String botId = jda.getSelfUser()
                            .getId();
                    final boolean isOwner = l.getOwner()
                            .equals(userId);

                    final boolean isDefault = l.getOwner()
                            .equals(botId);

                    return isOwner || isDefault;
                })
                .map(channelConfigEntityToDTO)
                .toList();
    }

    public ChannelConfig saveChannelConfig(final ChannelConfig channelConfig) {

        LOGGER.debug("Entered saveChannelConfig. channelConfig -> {}", channelConfig);
        final ChannelConfigEntity entity = buildNewChannelConfig(channelConfig);
        final ModelSettingsEntity modelSettings = modelSettingsRepository.save(entity.getModelSettings());
        entity.setModelSettings(modelSettings);

        return channelConfigEntityToDTO.apply(channelConfigRepository.save(entity));
    }

    public ChannelConfig updateChannelConfig(final String channelConfigId, final ChannelConfig channelConfig,
            final String userId) {

        LOGGER.debug("Entered updateChannelConfig. channelConfigId -> {}, userId -> {}, channelConfig -> {}",
                channelConfigId, userId, channelConfig);

        return channelConfigRepository.findById(channelConfigId)
                .map(c -> {
                    if (!c.getOwner()
                            .equals(userId)) {
                        throw new InsufficientPermissionException("Only the owner of a channel config can edit it");
                    }

                    return c;
                })
                .map(c -> buildUpdatedChannelConfig(channelConfigId, channelConfig, c))
                .map(c -> {
                    modelSettingsRepository.save(c.getModelSettings());
                    return channelConfigRepository.save(c);
                })
                .map(channelConfigEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException(("Error updating channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));
    }

    public void deleteChannelConfig(final String channelConfigId, final String userId) {

        LOGGER.debug("Entered deleteChannelConfig. channelConfigId -> {}, userId -> {}", channelConfigId, userId);
        channelConfigRepository.findById(channelConfigId)
                .map(c -> {
                    if (!c.getOwner()
                            .equals(userId)) {
                        throw new InsufficientPermissionException("Only the owner of a channel config can delete it");
                    }

                    return c;
                })
                .map(config -> {
                    channelRepository.findAllByChannelConfig(config)
                            .forEach(channelRepository::delete);

                    channelConfigRepository.delete(config);
                    return config;
                })
                .orElseThrow(() -> new ChannelConfigNotFoundException(("Error deleting channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));
    }

    private ChannelConfigEntity buildUpdatedChannelConfig(final String channelConfigId,
            final ChannelConfig newConfigInfo, final ChannelConfigEntity currentConfigInfo) {

        final ModerationSettingsEntity moderationSettings = Optional.ofNullable(newConfigInfo.getModerationSettings())
                .map(p -> moderationSettingsRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getModerationSettings()))
                .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings()));

        final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(newConfigInfo.getModelSettings());
        modelSettings.setId(currentConfigInfo.getModelSettings()
                .getId());

        final WorldEntity world = Optional.ofNullable(newConfigInfo.getWorld())
                .map(p -> worldRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getWorld()))
                .orElse(worldDTOToEntity.apply(World.defaultWorld()));

        final PersonaEntity persona = Optional.ofNullable(newConfigInfo.getPersona())
                .map(p -> personaRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getPersona()))
                .orElse(personaDTOToEntity.apply(Persona.defaultPersona()));

        return ChannelConfigEntity.builder()
                .id(channelConfigId)
                .name(newConfigInfo.getName())
                .owner(newConfigInfo.getOwner())
                .persona(persona)
                .world(world)
                .moderationSettings(moderationSettings)
                .modelSettings(modelSettings)
                .build();
    }

    private ChannelConfigEntity buildNewChannelConfig(ChannelConfig channelConfig) {

        final PersonaEntity persona = Optional.ofNullable(channelConfig.getPersona())
                .map(p -> personaRepository.findById(p.getId())
                        .orElse(personaDTOToEntity.apply(Persona.defaultPersona())))
                .orElse(personaDTOToEntity.apply(Persona.defaultPersona()));

        final ModerationSettingsEntity moderationSettings = Optional.ofNullable(channelConfig.getModerationSettings())
                .map(p -> moderationSettingsRepository.findById(p.getId())
                        .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings())))
                .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings()));

        final WorldEntity world = Optional.ofNullable(channelConfig.getWorld())
                .map(p -> worldRepository.findById(p.getId())
                        .orElse(worldDTOToEntity.apply(World.defaultWorld())))
                .orElse(worldDTOToEntity.apply(World.defaultWorld()));

        final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(channelConfig.getModelSettings());

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .owner(channelConfig.getOwner())
                .name(channelConfig.getName())
                .readPermissions(channelConfig.getReadPermissions())
                .writePermissions(channelConfig.getWritePermissions())
                .persona(persona)
                .world(world)
                .moderationSettings(moderationSettings)
                .modelSettings(modelSettings)
                .build();
    }
}
