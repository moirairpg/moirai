package es.thalesalv.chatrpg.core.application.model.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextModerationRequest {

    private final String input;

    public static TextModerationRequest build(String input) {
        return new TextModerationRequest(input);
    }
}
