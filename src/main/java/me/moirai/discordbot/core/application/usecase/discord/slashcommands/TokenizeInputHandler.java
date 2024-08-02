package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.domain.port.TokenizerPort;

@UseCaseHandler
public class TokenizeInputHandler extends AbstractUseCaseHandler<TokenizeInput, String> {

    private static final Logger LOG = LoggerFactory.getLogger(TokenizeInputHandler.class);

    private static final String TOKEN_REPLY_MESSAGE = "**Characters:** %s\n**Tokens:** %s\n**Token IDs:** %s (contains %s total tokens).";

    private final TokenizerPort tokenizerPort;

    public TokenizeInputHandler(TokenizerPort tokenizerPort) {
        this.tokenizerPort = tokenizerPort;
    }

    @Override
    public String execute(TokenizeInput useCase) {

        try {
            String tokens = tokenizerPort.tokenize(useCase.getInput());
            String tokenIds = Arrays.toString(tokenizerPort.getTokensIdsFrom(useCase.getInput()));
            int tokenCount = tokenizerPort.getTokenCountFrom(useCase.getInput());
            int characterCount = useCase.getInput().length();

            return String.format(TOKEN_REPLY_MESSAGE, characterCount, tokens, tokenIds, tokenCount);
        } catch (Exception e) {
            LOG.error("Error tokenizing input -> {}", useCase.getInput(), e);
            return "There was an error tokenizing the input.";
        }
    }
}
