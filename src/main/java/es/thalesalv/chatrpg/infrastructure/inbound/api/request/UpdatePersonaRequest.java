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
public class UpdatePersonaRequest {

    private String id;
    private final String name;
    private final String personality;
    private final String nudgeRole;
    private final String nudgeContent;
    private final String bumpRole;
    private final String bumpContent;
    private final String visibility;
    private final Integer bumpFrequency;
    private List<String> writerUsersToAdd;
    private List<String> writerUsersToRemove;
    private List<String> readerUsersToAdd;
    private List<String> readerUsersToRemove;
}
