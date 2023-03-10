package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.List;

import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.chatrpg.domain.model.openai.gpt.GptModelResponseChoice;
import es.thalesalv.chatrpg.domain.model.openai.gpt.GptModelResponseError;
import es.thalesalv.chatrpg.domain.model.openai.gpt.GptResponse;

public class OpenAiApiBuilder {

    public static Gpt3Request buildGpt3Request() {

        return Gpt3Request.builder()
                .maxTokens(100)
                .frequencyPenalty(0.2)
                .presencePenalty(0.2)
                .model("text-davinci-003")
                .prompt("This is a prompt!")
                .temperature(1D)
                .build();
    }

    public static GptResponse buildGptResponse() {

        final GptModelResponseChoice choice = GptModelResponseChoice.builder()
                .text("AI response text")
                .build();

        final List<GptModelResponseChoice> choices = new ArrayList<>();
        choices.add(choice);

        return GptResponse.builder()
                .id("fsdf")
                .model("text-davinci-003")
                .prompt("This is a prompt!")
                .choices(choices)
                .build();
    }

    public static GptResponse buildGptResponseEmptyText() {

        final GptModelResponseChoice choice = GptModelResponseChoice.builder().build();
        List<GptModelResponseChoice> choices = new ArrayList<>();
        choices.add(choice);

        return GptResponse.builder()
                .id("fsdf")
                .model("text-davinci-003")
                .prompt("This is a prompt!")
                .choices(choices)
                .build();
    }

    public static GptResponse buildGptResponse4xx() {

        final GptModelResponseChoice choice = GptModelResponseChoice.builder().build();
        List<GptModelResponseChoice> choices = new ArrayList<>();
        choices.add(choice);

        return GptResponse.builder()
                .id("fsdf")
                .model("text-davinci-003")
                .prompt("This is a prompt!")
                .error(GptModelResponseError.builder()
                        .message("0 is less than the minimum of 1 - 'n'")
                        .type("invalid_request_error")
                        .build())
                .build();
    }
}
