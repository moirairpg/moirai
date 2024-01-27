package es.thalesalv.chatrpg.core.domain.model.lorebook;

public interface LorebookEntryDomainService {

    LorebookEntry createLorebookEntry(String name, String description, String regex, String playerDiscordId);
}
