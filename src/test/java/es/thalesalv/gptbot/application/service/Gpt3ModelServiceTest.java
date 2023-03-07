package es.thalesalv.gptbot.application.service;

import java.util.ArrayList;

import es.thalesalv.gptbot.application.config.MessageEventData;
import net.dv8tion.jda.api.entities.Message;
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
import es.thalesalv.gptbot.application.translator.Gpt3RequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.gptbot.testutils.OpenAiApiBuilder;
import es.thalesalv.gptbot.testutils.PersonaBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class Gpt3ModelServiceTest {

    @Mock
    private ContextDatastore contextDatastore;

    @Mock
    private CommonErrorHandler commonErrorHandler;

    @Mock
    private Gpt3RequestTranslator gpt3RequestTranslator;

    @Mock
    private OpenAIApiService openAiService;

    @InjectMocks
    private Gpt3ModelService gpt3ModelService;

    @Test
    public void testGenerate_shouldProceed() {

        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Message message = Mockito.mock(Message.class);
        final MessageEventData eventData = PersonaBuilder.messageEventData();
        Mockito.when(gpt3RequestTranslator.buildRequest(anyString(), anyString(), any(Persona.class)))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request, eventData))
                .thenReturn(Mono.just(OpenAiApiBuilder.buildGptResponse()));

        StepVerifier.create(gpt3ModelService.generate(eventData, prompt, persona, new ArrayList<String>()))
                .assertNext(resp -> {
                    Assertions.assertEquals("AI response text", resp);
                }).verifyComplete();
    }

    @Test
    public void testGenerate_emptyAiResponse_shouldThrowError() {

        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Message message = Mockito.mock(Message.class);
        final MessageEventData eventData = PersonaBuilder.messageEventData();

        Mockito.when(gpt3RequestTranslator.buildRequest(prompt, "text-davinci-003", persona))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request, eventData))
                .thenReturn(Mono.just(OpenAiApiBuilder.buildGptResponseEmptyText()));

        StepVerifier.create(gpt3ModelService.generate(eventData, prompt, persona, new ArrayList<String>()))
                .verifyError(ModelResponseBlankException.class);
    }
}
