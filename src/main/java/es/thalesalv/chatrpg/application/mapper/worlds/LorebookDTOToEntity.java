package es.thalesalv.chatrpg.application.mapper.worlds;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Lorebook;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LorebookDTOToEntity implements Function<Lorebook, LorebookEntity> {

    @Override
    public LorebookEntity apply(Lorebook t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }
}
