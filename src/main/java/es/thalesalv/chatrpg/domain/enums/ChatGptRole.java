package es.thalesalv.chatrpg.domain.enums;

public enum ChatGptRole {

    SYSTEM("system"), ASSISTANT("assistant"), USER("user");

    private final String value;

    ChatGptRole(final String value) {

        this.value = value;
    }

    @Override
    public String toString() {

        return value;
    }
}
