package es.thalesalv.chatrpg.application.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
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
import es.thalesalv.chatrpg.domain.criteria.ChannelConfigSpecification;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.model.api.ChannelConfigPage;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.bot.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

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
                .map(channelConfigEntityToDTO)
                .filter(c -> this.hasReadAccessToConfig(userId, c))
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
                    modelSettingsRepository.delete(config.getModelSettings());
                    return config;
                })
                .orElseThrow(() -> new ChannelConfigNotFoundException(("Error deleting channel config: "
                        + CHANNEL_CONFIG_ID_NOT_FOUND.replace("CHANNEL_CONFIG_ID", channelConfigId))));
    }

    public ChannelConfigPage retrieveAllWithPagination(final String requesterDiscordId, final String searchCriteria,
            final String searchField, final int pageNumber, final int amountOfItems) {

        List<ChannelConfigEntity> allChannelConfigs;
        if (StringUtils.isBlank(searchField) || StringUtils.isBlank(searchCriteria)) {
            allChannelConfigs = channelConfigRepository.findAll();
        } else {
            final ChannelConfigSpecification spec = new ChannelConfigSpecification(searchField, searchCriteria);
            allChannelConfigs = channelConfigRepository.findAll(spec);
        }

        Collections.sort(allChannelConfigs, Comparator.comparing(ChannelConfigEntity::getName));
        return buildChannelConfigPage(allChannelConfigs, pageNumber, amountOfItems, requesterDiscordId);
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

    private boolean hasReadAccessToConfig(final String requesterDiscordId, final ChannelConfig channelConfig) {

        final String botId = jda.getSelfUser()
                .getId();
        final boolean isOwner = channelConfig.getOwner()
                .equals(requesterDiscordId);

        final boolean isDefault = channelConfig.getOwner()
                .equals(botId);

        return isOwner || isDefault;
    }

    private ChannelConfigPage buildChannelConfigPage(final List<ChannelConfigEntity> allChannelConfigs,
            final int pageNumber, final int amountOfItems, final String requesterDiscordId) {

        final int numberOfPages = (int) Math.ceil((double) allChannelConfigs.size() / amountOfItems);
        final List<ChannelConfig> channelConfigPage = ListUtils.partition(allChannelConfigs, amountOfItems)
                .get(pageNumber - 1)
                .stream()
                .map(channelConfigEntityToDTO)
                .filter(c -> this.hasReadAccessToConfig(requesterDiscordId, c))
                .collect(Collectors.toList());

        return ChannelConfigPage.builder()
                .currentPage(pageNumber)
                .numberOfPages(numberOfPages)
                .channelConfigs(channelConfigPage)
                .totalNumberOfItems(allChannelConfigs.size())
                .numberOfItemsInPage(channelConfigPage.size())
                .build();
    }
}
