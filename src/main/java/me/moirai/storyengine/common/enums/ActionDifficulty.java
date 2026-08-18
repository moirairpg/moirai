package me.moirai.storyengine.common.enums;

public enum ActionDifficulty {

    EASY(8, 5),
    MEDIUM(12, 10),
    HARD(16, 20),
    VERY_HARD(20, 30),
    FORMIDABLE(24, 50);

    private final int dc;
    private final int successXp;

    private ActionDifficulty(int dc, int successXp) {
        this.dc = dc;
        this.successXp = successXp;
    }

    public int getDc() {
        return dc;
    }

    public int getSuccessXp() {
        return successXp;
    }

    public int getFailureXp() {
        return successXp / 2;
    }
}
