package me.moirai.discordbot.infrastructure.inbound.api.request;

import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchDirection;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchOperation;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchSortingField;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchVisibility;

public class PersonaSearchParameters {

    private String name;
    private String ownerDiscordId;
    private boolean favorites;
    private Integer page;
    private Integer size;
    private SearchSortingField sortingField;
    private SearchDirection direction;
    private SearchVisibility visibility;
    private SearchOperation operation;

    public PersonaSearchParameters() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerDiscordId() {
        return ownerDiscordId;
    }

    public void setOwnerDiscordId(String ownerDiscordId) {
        this.ownerDiscordId = ownerDiscordId;
    }

    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public SearchSortingField getSortingField() {
        return sortingField;
    }

    public void setSortingField(SearchSortingField sortingField) {
        this.sortingField = sortingField;
    }

    public SearchDirection getDirection() {
        return direction;
    }

    public void setDirection(SearchDirection direction) {
        this.direction = direction;
    }

    public SearchVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(SearchVisibility visibility) {
        this.visibility = visibility;
    }

    public SearchOperation getOperation() {
        return operation;
    }

    public void setOperation(SearchOperation operation) {
        this.operation = operation;
    }
}
