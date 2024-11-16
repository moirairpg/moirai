package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

public class TokenizeResultFixture {

    public static TokenizeResult.Builder create() {

        return TokenizeResult.builder()
                .characterCount(10)
                .tokenCount(10)
                .tokenIds(new long[] { 1, 2, 3 })
                .tokens("This is an input.");
    }
}
