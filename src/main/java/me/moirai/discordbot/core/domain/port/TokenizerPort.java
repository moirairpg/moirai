package me.moirai.discordbot.core.domain.port;

import java.io.UnsupportedEncodingException;

import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;

public interface TokenizerPort {

    long[] getTokensIdsFrom(String text);

    long[] getTokensIdsFrom(String[] text);

    int getTokenCountFrom(String text);

    int getTokenCountFrom(String[] text);

    String getTokens(String text) throws UnsupportedEncodingException;

    TokenizeResult tokenize(String text) throws UnsupportedEncodingException;
}
