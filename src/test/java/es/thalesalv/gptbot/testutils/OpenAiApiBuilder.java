package es.thalesalv.gptbot.testutils;

import java.util.ArrayList;
import java.util.List;

import es.thalesalv.gptbot.domain.model.openai.gpt.GptModelResponseChoice;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;

public class OpenAiApiBuilder {

    public static GptRequest buildGptRequest() {

        return GptRequest.builder()
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
}
