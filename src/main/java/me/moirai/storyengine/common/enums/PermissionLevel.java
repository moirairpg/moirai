package me.moirai.storyengine.common.enums;

public enum PermissionLevel {
    READ,
    WRITE,
    OWNER;

    public PermissionLevel weakest(PermissionLevel other) {
        return ordinal() <= other.ordinal() ? this : other;
    }
}
