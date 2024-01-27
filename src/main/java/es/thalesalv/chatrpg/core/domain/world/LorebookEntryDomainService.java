package es.thalesalv.chatrpg.core.domain.world;

public interface LorebookEntryDomainService {

    LorebookEntry createLorebookEntry(String name, String description, String regex, String playerDiscordId);
}
