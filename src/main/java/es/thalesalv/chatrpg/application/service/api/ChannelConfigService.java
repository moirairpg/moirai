package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.Settings;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelConfigService {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final WorldDTOToEntity worldDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ChannelDTOToEntity channelDTOToEntity;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelConfigEntityToDTO channelConfigEntityToDTO;

    private final PersonaRepository personaRepository;
    private final WorldRepository worldRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigService.class);

    public List<Channel> retrieveAllChannels() {

        LOGGER.debug("Retrieving channel data from request");
        return channelRepository.findAll()
                .stream()
                .map(channelEntityToDTO)
                .toList();
    }

    public List<Channel> retrieveChannelConfigsByChannelId(final String channelId) {

        LOGGER.debug("Retrieving channel by ID data from request. channelId -> {}", channelId);
        return channelRepository.findById(channelId)
                .map(channelEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(ChannelConfigNotFoundException::new);
    }

    public List<Channel> saveChannel(final Channel channel) {

        LOGGER.debug("Saving channel data from request");
        return channelConfigRepository.findById(channel.getChannelConfig()
                .getId())
                .map(c -> {
                    channel.setChannelConfig(channelConfigEntityToDTO.apply(c));
                    return channelDTOToEntity.apply(channel);
                })
                .map(channelRepository::save)
                .map(channelEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(ChannelConfigNotFoundException::new);
    }

    public List<Channel> updateChannel(final String channelId, final Channel channel) {

        LOGGER.debug("Updating channel data from request. channelId -> {}", channelId);
        return channelRepository.findById(channelId)
                .map(c -> {
                    c.setChannelConfig(channelConfigRepository.findById(channel.getChannelConfig()
                            .getId())
                            .orElseThrow(() -> new ChannelConfigNotFoundException(
                                    "The configuration requested does not exist")));

                    return c;
                })
                .map(channelRepository::save)
                .map(channelEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new ChannelConfigNotFoundException(
                        "The requested channel does not have an entry saved."));
    }

    public void deleteChannel(final String channelId) {

        LOGGER.debug("Deleting channel data from request");
        channelRepository.deleteById(channelId);
    }

    public List<ChannelConfig> retrieveAllChannelConfigs() {

        LOGGER.debug("Retrieving all available channel configs");
        return channelConfigRepository.findAll()
                .stream()
                .map(channelConfigEntityToDTO)
                .toList();
    }

    public List<ChannelConfig> retrieveChannelConfigById(final String channelConfigId) {

        LOGGER.debug("Retrieving channel config by ID data from request");
        return channelConfigRepository.findById(channelConfigId)
                .map(channelConfigEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(ChannelConfigNotFoundException::new);
    }

    public List<ChannelConfig> saveChannelConfig(final ChannelConfig channelConfig) {

        LOGGER.debug("Saving channel config data from request");
        return Optional.of(buildNewChannelConfig(channelConfig))
                .map(channelConfigRepository::save)
                .map(channelConfigEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new RuntimeException("Error saving channel config"));
    }

    public List<ChannelConfig> updateChannelConfig(final String channelConfigId, final ChannelConfig channelConfig) {

        LOGGER.debug("Updating channel config data from request. channelConfigId -> {}", channelConfigId);
        return channelConfigRepository.findById(channelConfigId)
                .map(c -> buildUpdatedChannelConfig(channelConfigId, channelConfig, c))
                .map(channelConfigRepository::save)
                .map(channelConfigEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new ChannelConfigNotFoundException(
                        "The channel config with the requested ID could not be found"));
    }

    public void deleteChannelConfig(final String channelConfigId) {

        LOGGER.debug("Deleting channel config data from request. channelConfigId -> {}", channelConfigId);
        channelConfigRepository.findById(channelConfigId)
                .map(config -> {
                    channelRepository.findAllByChannelConfig(config)
                            .forEach(channelRepository::delete);

                    channelConfigRepository.delete(config);
                    return config;
                })
                .orElseThrow(() -> new ChannelConfigNotFoundException("Channel config request for deletion not found"));
    }

    private ChannelConfigEntity buildUpdatedChannelConfig(final String channelConfigId,
            final ChannelConfig newConfigInfo, final ChannelConfigEntity currentConfigInfo) {

        final Settings settings = Optional.ofNullable(newConfigInfo.getSettings())
                .orElse(Settings.defaultSettings());

        final ModerationSettingsEntity moderationSettings = Optional.ofNullable(settings.getModerationSettings())
                .map(p -> moderationSettingsRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getModerationSettings()))
                .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings()));

        final ModelSettingsEntity modelSettings = Optional.ofNullable(settings.getModelSettings())
                .map(p -> modelSettingsRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getModelSettings()))
                .orElse(modelSettingsDTOToEntity.apply(ModelSettings.defaultModelSettings()));

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
                .editPermissions(newConfigInfo.getEditPermissions())
                .owner(newConfigInfo.getOwner())
                .persona(persona)
                .world(world)
                .moderationSettings(moderationSettings)
                .modelSettings(modelSettings)
                .build();
    }

    private ChannelConfigEntity buildNewChannelConfig(ChannelConfig channelConfig) {

        final Settings settings = Optional.ofNullable(channelConfig.getSettings())
                .orElse(Settings.defaultSettings());

        final PersonaEntity persona = Optional.ofNullable(channelConfig.getPersona())
                .map(p -> personaRepository.findById(p.getId())
                        .orElse(personaDTOToEntity.apply(Persona.defaultPersona())))
                .orElse(personaDTOToEntity.apply(Persona.defaultPersona()));

        final ModerationSettingsEntity moderationSettings = Optional.ofNullable(settings.getModerationSettings())
                .map(p -> moderationSettingsRepository.findById(p.getId())
                        .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings())))
                .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings()));

        final ModelSettingsEntity modelSettings = Optional.ofNullable(settings.getModelSettings())
                .map(p -> modelSettingsRepository.findById(p.getId())
                        .orElse(modelSettingsDTOToEntity.apply(ModelSettings.defaultModelSettings())))
                .orElse(modelSettingsDTOToEntity.apply(ModelSettings.defaultModelSettings()));

        final WorldEntity world = Optional.ofNullable(channelConfig.getWorld())
                .map(p -> worldRepository.findById(p.getId())
                        .orElse(worldDTOToEntity.apply(World.defaultWorld())))
                .orElse(worldDTOToEntity.apply(World.defaultWorld()));

        return ChannelConfigEntity.builder()
                .editPermissions(channelConfig.getEditPermissions())
                .owner(channelConfig.getOwner())
                .persona(persona)
                .world(world)
                .moderationSettings(moderationSettings)
                .modelSettings(modelSettings)
                .build();
    }
}
