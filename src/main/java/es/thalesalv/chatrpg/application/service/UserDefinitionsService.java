package es.thalesalv.chatrpg.application.service;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.UserDefinitionsEntity;
import es.thalesalv.chatrpg.adapters.data.repository.UserDefinitionsRepository;
import es.thalesalv.chatrpg.application.mapper.UserDefinitionsToDTO;
import es.thalesalv.chatrpg.application.mapper.UserDefinitionsToEntity;
import es.thalesalv.chatrpg.domain.exception.NotFoundException;
import es.thalesalv.chatrpg.domain.model.bot.UserDefinitions;
import es.thalesalv.chatrpg.domain.model.discord.DiscordUserData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDefinitionsService {

    private final UserDefinitionsToEntity userDefinitionsToEntity;
    private final UserDefinitionsToDTO userDefinitionsToDTO;
    private final UserDefinitionsRepository userDefinitionsRepository;

    public UserDefinitions retrieveUserDefinitions(final String userId) {

        return userDefinitionsRepository.findById(userId)
                .map(userDefinitionsToDTO)
                .orElseThrow(() -> new NotFoundException("The requested user does not exist"));
    }

    public UserDefinitions updateUserDefinitions(final UserDefinitions userDefinitions) {

        final UserDefinitionsEntity entity = userDefinitionsToEntity.apply(userDefinitions);
        return userDefinitionsToDTO.apply(userDefinitionsRepository.save(entity));
    }

    public void persistUser(DiscordUserData user) {

        if (!userDefinitionsRepository.existsById(user.getId())) {
            userDefinitionsRepository.save(UserDefinitionsEntity.builder()
                    .id(user.getId())
                    .build());
        }
    }
}
