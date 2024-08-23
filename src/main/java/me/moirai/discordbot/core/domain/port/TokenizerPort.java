package me.moirai.discordbot.core.domain.port;

import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;

public interface TokenizerPort {

    long[] getTokensIdsFrom(String text);

    int getTokenCountFrom(String text);

    String getTokens(String text);

    TokenizeResult tokenize(String text);
}
