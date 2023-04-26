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
    private final ChannelDTOToEntity channelDTOToEntity;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelConfigEntityToDTO channelConfigEntityToDTO;

    private final PersonaRepository personaRepository;
    private final WorldRepository worldRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final String DEFAULT_ID = "0";
    private static final String CHANNEL_CONFIG_ID_NOT_FOUND = "Channel config with id CHANNEL_CONFIG_ID could not be found in database.";
    private static final String CHANNEL_ID_NOT_FOUND = "discord channel with id CHANNEL_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigService.class);

    public List<Channel> retrieveAllChannels() {

        LOGGER.debug("Retrieving channel data from request");
        return channelRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(channelEntityToDTO)
                .toList();
    }

    public Channel retrieveChannelConfigByChannelId(final String channelId) {

        LOGGER.debug("Retrieving channel by ID data from request. channelId -> {}", channelId);
        return channelRepository.findById(channelId)
                .map(channelEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException(
                        "Error retrieving channel by ID: " + CHANNEL_ID_NOT_FOUND.replace("CHANNEL_ID", channelId)));
    }

    public Channel saveChannel(final Channel channel) {

        LOGGER.debug("Saving channel data from request");
        final String channelConfigId = channel.getChannelConfig()
                .getId();

        return channelConfigRepository.findById(channelConfigId)
                .map(c -> {
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));
                    channel.setChannelConfig(channelConfigEntityToDTO.apply(c));
                    return channelDTOToEntity.apply(channel);
                })
                .map(channelRepository::save)
                .map(channelEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException("Error saving channel"));
    }

    public Channel updateChannel(final String channelId, final Channel channel) {

        LOGGER.debug("Updating channel data from request. channelId -> {}", channelId);
        return channelRepository.findById(channelId)
                .map(c -> {
                    final String channelConfigId = channel.getChannelConfig()
                            .getId();

                    c.setChannelConfig(channelConfigRepository.findById(channelConfigId)
                            .orElseThrow(() -> new ChannelConfigNotFoundException(
                                    CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));

                    c.getChannelConfig()
                            .setOwner(Optional.ofNullable(c.getChannelConfig()
                                    .getOwner())
                                    .orElse(jda.getSelfUser()
                                            .getId()));
                    return c;
                })
                .map(channelRepository::save)
                .map(channelEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException(
                        "Error updating channel wih ID: " + CHANNEL_ID_NOT_FOUND.replace("CHANNEL_ID", channelId)));
    }

    public void deleteChannel(final String channelId) {

        LOGGER.debug("Deleting channel data from request. channelId -> {}", channelId);
        channelRepository.deleteById(channelId);
    }

    public List<ChannelConfig> retrieveAllChannelConfigs() {

        LOGGER.debug("Retrieving all available channel configs");
        return channelConfigRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(channelConfigEntityToDTO)
                .toList();
    }

    public ChannelConfig retrieveChannelConfigById(final String channelConfigId) {

        LOGGER.debug("Retrieving channel config by ID data from request -> {}", channelConfigId);
        return channelConfigRepository.findById(channelConfigId)
                .map(channelConfigEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException("Error retrieving channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId)));
    }

    public ChannelConfig saveChannelConfig(final ChannelConfig channelConfig) {

        LOGGER.debug("Saving channel config data from request");
        return Optional.of(buildNewChannelConfig(channelConfig))
                .map(channelConfigRepository::save)
                .map(channelConfigEntityToDTO)
                .orElseThrow(() -> new RuntimeException("Error saving channel config"));
    }

    public ChannelConfig updateChannelConfig(final String channelConfigId, final ChannelConfig channelConfig) {

        LOGGER.debug("Updating channel config data from request. channelConfigId -> {}", channelConfigId);
        return channelConfigRepository.findById(channelConfigId)
                .map(c -> buildUpdatedChannelConfig(channelConfigId, channelConfig, c))
                .map(channelConfigRepository::save)
                .map(channelConfigEntityToDTO)
                .orElseThrow(() -> new ChannelConfigNotFoundException(("Error updating channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));
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
                .orElseThrow(() -> new ChannelConfigNotFoundException(("Error deleting channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));
    }

    private ChannelConfigEntity buildUpdatedChannelConfig(final String channelConfigId,
            final ChannelConfig newConfigInfo, final ChannelConfigEntity currentConfigInfo) {

        final ModerationSettingsEntity moderationSettings = Optional.ofNullable(newConfigInfo.getModerationSettings())
                .map(p -> moderationSettingsRepository.findById(p.getId())
                        .orElse(currentConfigInfo.getModerationSettings()))
                .orElse(moderationSettingsDTOToEntity.apply(ModerationSettings.defaultModerationSettings()));

        final ModelSettingsEntity modelSettings = Optional.ofNullable(newConfigInfo.getModelSettings())
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

        final ModelSettingsEntity modelSettings = Optional.ofNullable(channelConfig.getModelSettings())
                .map(p -> modelSettingsRepository.findById(p.getId())
                        .orElse(modelSettingsDTOToEntity.apply(ModelSettings.defaultModelSettings())))
                .orElse(modelSettingsDTOToEntity.apply(ModelSettings.defaultModelSettings()));

        final WorldEntity world = Optional.ofNullable(channelConfig.getWorld())
                .map(p -> worldRepository.findById(p.getId())
                        .orElse(worldDTOToEntity.apply(World.defaultWorld())))
                .orElse(worldDTOToEntity.apply(World.defaultWorld()));

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .owner(channelConfig.getOwner())
                .persona(persona)
                .world(world)
                .moderationSettings(moderationSettings)
                .modelSettings(modelSettings)
                .build();
    }
}
