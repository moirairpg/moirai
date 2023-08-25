package es.thalesalv.chatrpg.application.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.UserDefinitionsEntity;
import es.thalesalv.chatrpg.domain.model.bot.UserDefinitions;

@Component
public class UserDefinitionsToDTO implements Function<UserDefinitionsEntity, UserDefinitions> {

    @Override
    public UserDefinitions apply(UserDefinitionsEntity userDefinitionsEntity) {

        return UserDefinitions.builder()
                .id(userDefinitionsEntity.getId())
                .build();
    }
}
