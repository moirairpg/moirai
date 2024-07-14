package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

public class PersonaSearchParameters {

    private Integer page;
    private Integer items;
    private String sortByField;
    private String direction;
    private String name;
    private String gameMode;

    public PersonaSearchParameters() {
    }

    public Integer getPage() {
        return page;
    }

    public Integer getItems() {
        return items;
    }

    public String getSortByField() {
        return sortByField;
    }

    public String getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setItems(Integer items) {
        this.items = items;
    }

    public void setSortByField(String sortByField) {
        this.sortByField = sortByField;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
}
