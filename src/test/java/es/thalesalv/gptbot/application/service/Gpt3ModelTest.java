package es.thalesalv.gptbot.application.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.errorhandling.CommonErrorHandler;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import es.thalesalv.gptbot.testutils.OpenAiApiBuilder;
import es.thalesalv.gptbot.testutils.PersonaBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class Gpt3ModelTest {

    @Mock
    private ContextDatastore contextDatastore;

    @Mock
    private CommonErrorHandler commonErrorHandler;

    @Mock
    private GptRequestTranslator gptRequestTranslator;

    @Mock
    private OpenAIApiService openAiService;

    @InjectMocks
    private Gpt3Model gpt3Model;

    @Test
    public void testGenerate_shouldProceed() {

        final GptRequest request = OpenAiApiBuilder.buildGptRequest();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Mono<GptResponse> monoResponse = Mono.just(OpenAiApiBuilder.buildGptResponse());
        Mockito.when(gptRequestTranslator.buildRequest(prompt, "text-davinci-003", persona))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request))
                .thenReturn(monoResponse);

        StepVerifier.create(gpt3Model.generate(prompt, persona))
                .assertNext(resp -> {
                    Assertions.assertEquals("AI response text", resp);
                }).verifyComplete();
    }

    @Test
    public void testGenerate_emptyAiResponse_shouldThrowError() {

        final GptRequest request = OpenAiApiBuilder.buildGptRequest();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Mono<GptResponse> monoResponse = Mono.just(OpenAiApiBuilder.buildGptResponseEmptyText());
        Mockito.when(gptRequestTranslator.buildRequest(prompt, "text-davinci-003", persona))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request))
                .thenReturn(monoResponse);

        StepVerifier.create(gpt3Model.generate(prompt, persona))
                .verifyError(ModelResponseBlankException.class);
    }
}
