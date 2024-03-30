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
public class UpdateWorldRequest {

    private String name;
    private String description;
    private String adventureStart;
    private String visibility;
    private List<String> writerUsersToAdd;
    private List<String> writerUsersToRemove;
    private List<String> readerUsersToAdd;
    private List<String> readerUsersToRemove;
}
