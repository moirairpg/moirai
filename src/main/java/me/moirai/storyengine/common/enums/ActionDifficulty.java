package me.moirai.storyengine.common.enums;

public enum ActionDifficulty {

    EASY(8),
    MEDIUM(12),
    HARD(16),
    VERY_HARD(20),
    FORMIDABLE(24);

    private final int dc;

    private ActionDifficulty(int dc) {
        this.dc = dc;
    }

    public int getDc() {
        return dc;
    }
}
