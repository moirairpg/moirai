package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaSearchParameters {

    private Integer page;
    private Integer items;
    private String sortByField;
    private String direction;
    private String name;
    private String gameMode;
}
