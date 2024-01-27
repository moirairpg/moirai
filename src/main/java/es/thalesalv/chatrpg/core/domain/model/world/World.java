package es.thalesalv.chatrpg.core.domain.model.world;

import static es.thalesalv.chatrpg.core.domain.model.Visibility.PRIVATE;
import static es.thalesalv.chatrpg.core.domain.model.Visibility.PUBLIC;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.exception.BusinessException;
import es.thalesalv.chatrpg.core.domain.model.Permissions;
import es.thalesalv.chatrpg.core.domain.model.Visibility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class World {

    private String id;
    private String name;
    private String description;
    private String initialPrompt;
    private Lorebook lorebook;
    private Visibility visibility;
    private Permissions permissions;

    private World(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.initialPrompt = builder.initialPrompt;
        this.lorebook = builder.lorebook;
        this.visibility = builder.visibility;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {

        return new Builder();
    }

    public Lorebook getLorebook() {

        return lorebook;
    }

    public void makePublic() {

        this.visibility = PUBLIC;
    }

    public void makePrivate() {

        this.visibility = PRIVATE;
    }

    public boolean isPublic() {

        return this.visibility.equals(PUBLIC);
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updateDescription(String description) {

        this.description = description;
    }

    public void updateInitialPrompt(String initialPrompt) {

        this.initialPrompt = initialPrompt;
    }

    public void addLorebookEntryToLorebook(String lorebookEntryId) {

        Lorebook updatedLorebook = lorebook.addLorebookEntry(lorebookEntryId);
        this.lorebook = updatedLorebook;
    }

    public void removeLorebookEntryFromLorebook(String lorebookEntryId) {

        Lorebook updatedLorebook = lorebook.removeLorebookEntry(lorebookEntryId);
        this.lorebook = updatedLorebook;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String description;
        private String initialPrompt;
        private Lorebook lorebook;
        private Visibility visibility;
        private Permissions permissions;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder description(String description) {

            this.description = description;
            return this;
        }

        public Builder initialPrompt(String initialPrompt) {

            this.initialPrompt = initialPrompt;
            return this;
        }

        public Builder lorebook(Lorebook lorebook) {

            this.lorebook = lorebook;
            return this;
        }

        public Builder visibility(Visibility visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder permissions(Permissions permissions) {

            this.permissions = permissions;
            return this;
        }

        public World build() {

            if (StringUtils.isBlank(name)) {
                throw new BusinessException("Persona name cannot be null or empty");
            }

            if (visibility == null) {
                throw new BusinessException("Visibility cannot be null");
            }

            if (permissions == null) {
                throw new BusinessException("Permissions cannot be null");
            }

            return new World(this);
        }
    }
}
