package es.thalesalv.chatrpg.core.domain.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public final class Lorebook {

    private final List<String> lorebookEntryIds;

    public Lorebook(Builder builder) {

        List<String> unmodifiableListOfEntries = Collections
                .unmodifiableList(new ArrayList<>(builder.lorebookEntryIds));

        this.lorebookEntryIds = unmodifiableListOfEntries;
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(Lorebook lorebook) {

        return builder().lorebookEntryIds(lorebook.getLorebookEntryIds());
    }

    public Lorebook addLorebookEntry(String lorebookEntry) {

        List<String> lorebookEntries = new ArrayList<>(this.lorebookEntryIds);
        lorebookEntries.add(lorebookEntry);

        return cloneFrom(this).lorebookEntryIds(lorebookEntries).build();
    }

    public Lorebook removeLorebookEntry(String lorebookEntry) {

        List<String> lorebookEntries = new ArrayList<>(this.lorebookEntryIds);
        lorebookEntries.remove(lorebookEntry);

        return cloneFrom(this).lorebookEntryIds(lorebookEntries).build();
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private List<String> lorebookEntryIds;

        public Builder lorebookEntryIds(List<String> lorebookEntryIds) {

            this.lorebookEntryIds = lorebookEntryIds;
            return this;
        }

        public Lorebook build() {

            return new Lorebook(this);
        }
    }
}
