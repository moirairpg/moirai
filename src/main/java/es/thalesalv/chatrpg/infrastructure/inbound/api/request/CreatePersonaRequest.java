package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class CreatePersonaRequest {

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
}
