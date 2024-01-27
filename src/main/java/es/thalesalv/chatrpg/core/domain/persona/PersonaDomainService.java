package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;

public interface PersonaDomainService {
    
    Persona createPersona(String name, String personality, Permissions permissions, Visibility visibility);
}
