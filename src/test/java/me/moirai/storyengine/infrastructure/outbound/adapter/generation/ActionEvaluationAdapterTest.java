package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.jackson.databind.json.JsonMapper;

import me.moirai.storyengine.AbstractWebMockTest;
import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;

class ActionEvaluationAdapterTest extends AbstractWebMockTest {

    private ActionEvaluationAdapter adapter;

    @BeforeEach
    void before() {

        var restClient = RestClient.builder()
                .baseUrl("http://localhost:" + PORT)
                .build();

        adapter = new ActionEvaluationAdapter("/responses", "test-token", restClient, JsonMapper.builder().build());
    }

    @Test
    void shouldReturnTheTypedVerdictWhenTheResponseIsValid() throws JsonProcessingException {

        // given
        prepareWebserverFor(responseWithOutputText(
                "{\"verdict\":\"CHECK\",\"attribute\":null,\"skill\":\"STEALTH\",\"difficulty\":\"HARD\",\"reason\":\"Real stakes against a deadly foe.\"}"),
                200);

        // when
        var result = adapter.evaluateAction(evaluationRequest());

        // then
        assertThat(result.verdict()).isEqualTo(ActionVerdict.CHECK);
        assertThat(result.attribute()).isNull();
        assertThat(result.skill()).isEqualTo("STEALTH");
        assertThat(result.difficulty()).isEqualTo(ActionDifficulty.HARD);
        assertThat(result.reason()).isEqualTo("Real stakes against a deadly foe.");
    }

    @Test
    void shouldReturnTheAttributeWhenTheVerdictNamesOne() throws JsonProcessingException {

        // given
        prepareWebserverFor(responseWithOutputText(
                "{\"verdict\":\"CHECK\",\"attribute\":\"STRENGTH\",\"skill\":null,\"difficulty\":\"MEDIUM\",\"reason\":\"Raw might.\"}"),
                200);

        // when
        var result = adapter.evaluateAction(evaluationRequest());

        // then
        assertThat(result.verdict()).isEqualTo(ActionVerdict.CHECK);
        assertThat(result.attribute()).isEqualTo(CharacterAttribute.STRENGTH);
        assertThat(result.skill()).isNull();
    }

    @Test
    void shouldReturnNullComponentsWhenTheResponseNamesUnknownValues() throws JsonProcessingException {

        // given
        prepareWebserverFor(responseWithOutputText(
                "{\"verdict\":\"NO_CHECK\",\"attribute\":\"LUCK\",\"skill\":\"COOKING\",\"difficulty\":\"TRIVIAL\",\"reason\":\"Routine action.\"}"),
                200);

        // when
        var result = adapter.evaluateAction(evaluationRequest());

        // then
        assertThat(result.verdict()).isEqualTo(ActionVerdict.NO_CHECK);
        assertThat(result.attribute()).isNull();
        assertThat(result.skill()).isNull();
        assertThat(result.difficulty()).isNull();
    }

    @Test
    void shouldThrowWhenTheResponseBodyIsNotTheExpectedShape() throws JsonProcessingException {

        // given
        prepareWebserverFor(responseWithOutputText("not a structured verdict"), 200);

        // then
        assertThatThrownBy(() -> adapter.evaluateAction(evaluationRequest()))
                .isInstanceOf(RuntimeException.class);
    }

    private ActionEvaluationRequest evaluationRequest() {

        return new ActionEvaluationRequest(
                MessagePrompt.ACTION_EVALUATOR.getText(),
                List.of(ChatMessage.asUser("I sneak past the sleeping dragon to reach the treasure")));
    }

    private OpenAiResponsesApiResponse responseWithOutputText(String outputText) {

        var outputContent = new OpenAiResponsesApiOutputContent();
        outputContent.setText(outputText);

        var output = new OpenAiResponsesApiOutput();
        output.setContent(Collections.singletonList(outputContent));

        var usage = new OpenAiResponsesApiUsage();
        usage.setInputTokens(10);
        usage.setOutputTokens(20);
        usage.setTotalTokens(30);

        var response = new OpenAiResponsesApiResponse();
        response.setOutput(Collections.singletonList(output));
        response.setUsage(usage);

        return response;
    }
}
