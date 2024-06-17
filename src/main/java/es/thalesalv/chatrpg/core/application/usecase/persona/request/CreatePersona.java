package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import java.util.List;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.CreatePersonaResult;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
@Builder(builderClassName = "Builder")
public final class CreatePersona extends UseCase<Mono<CreatePersonaResult>> {

    private final String name;
    private final String personality;
    private final String nudgeRole;
    private final String nudgeContent;
    private final String bumpRole;
    private final String bumpContent;
    private final String visibility;
    private final String gameMode;
    private final Integer bumpFrequency;
    private final List<String> usersAllowedToWrite;
    private final List<String> usersAllowedToRead;
    private final String requesterDiscordId;
}
