package es.thalesalv.chatrpg.application.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.UserDefinitionsEntity;
import es.thalesalv.chatrpg.domain.model.bot.UserDefinitions;

@Component
public class UserDefinitionsToEntity implements Function<UserDefinitions, UserDefinitionsEntity> {

    @Override
    public UserDefinitionsEntity apply(UserDefinitions userDefinitionsEntity) {

        return UserDefinitionsEntity.builder()
                .id(userDefinitionsEntity.getId())
                .build();
    }
}
