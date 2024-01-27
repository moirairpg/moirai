package es.thalesalv.chatrpg.core.domain.model.persona;

import es.thalesalv.chatrpg.core.domain.model.Permissions;
import es.thalesalv.chatrpg.core.domain.model.Visibility;

public interface PersonaDomainService {
    
    Persona createPersona(String name, String personality, Permissions permissions, Visibility visibility);
}
