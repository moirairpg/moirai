package es.thalesalv.chatrpg.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.adapters.data.ContextDatastore;
import es.thalesalv.chatrpg.adapters.rest.OpenAIApiService;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.application.service.Gpt3ModelService;
import es.thalesalv.chatrpg.application.service.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.translator.Gpt3RequestTranslator;
import es.thalesalv.chatrpg.domain.exception.ModelResponseBlankException;
import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.chatrpg.testutils.OpenAiApiBuilder;
import es.thalesalv.chatrpg.testutils.PersonaBuilder;
import net.dv8tion.jda.api.entities.Message;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class Gpt3ModelServiceTest {

    @Mock
    private ModerationService moderationService;

    @Mock
    private MessageFormatHelper messageFormatHelper;

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
    @Disabled
    public void testGenerate_shouldProceed() {

        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Message message = Mockito.mock(Message.class);
        final MessageEventData eventData = PersonaBuilder.messageEventData();
        eventData.setPersona(persona);

        Mockito.when(gpt3RequestTranslator.buildRequest(anyString(), any(Persona.class)))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request, eventData))
                .thenReturn(Mono.just(OpenAiApiBuilder.buildGptResponse()));

        StepVerifier.create(gpt3ModelService.generate(prompt, new ArrayList<String>(), eventData))
                .assertNext(resp -> {
                    Assertions.assertEquals("AI response text", resp);
                }).verifyComplete();
    }

    @Test
    @Disabled
    public void testGenerate_emptyAiResponse_shouldThrowError() {

        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final String prompt = "This is a prompt!";
        final Persona persona = PersonaBuilder.persona();
        final Message message = Mockito.mock(Message.class);
        final MessageEventData eventData = PersonaBuilder.messageEventData();
        eventData.setPersona(persona);

        Mockito.when(gpt3RequestTranslator.buildRequest(prompt, persona))
                .thenReturn(request);

        Mockito.when(openAiService.callGptApi(request, eventData))
                .thenReturn(Mono.just(OpenAiApiBuilder.buildGptResponseEmptyText()));

        StepVerifier.create(gpt3ModelService.generate(prompt, new ArrayList<String>(), eventData))
                .verifyError(ModelResponseBlankException.class);
    }
}
