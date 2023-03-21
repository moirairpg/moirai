package es.thalesalv.chatrpg.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.regex.Pattern;

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
import es.thalesalv.chatrpg.application.service.helper.MessageFormatHelper;
import es.thalesalv.chatrpg.application.translator.Gpt3RequestTranslator;
import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.chatrpg.testutils.OpenAiApiBuilder;
import es.thalesalv.chatrpg.testutils.PersonaBuilder;
import net.dv8tion.jda.api.entities.Message;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Disabled
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
                    assertEquals("AI response text", resp);
                }).verifyComplete();
    }

    @Test
    public void testProcessors() {
        String inputText = "Without thinking, Anne turns around and charges towards her window. As she reaches it, she flings it open and climbs out onto the roof. \n" +
                "\n" +
                "She doesn't realize that the only thing covering her is her panties until the cool night air hits her bare skin. Anne tries to cover her nakedness with her hands as she scrambles out the open window.\n" +
                "\n" +
                "Once outside, Anne hears the monster roar in anger. She can feel its hot breath on her back as she slides down the roof and lands on her feet, running towards the front of the house. \n" +
                "\n" +
                "Anne doesn't stop until she reaches the street corner, where she finally takes a moment to catch her breath. She looks back at her house and sees a dark figure emerge from her bedroom window. It climbs down onto the roof, searching for its next victim";
        String expected = "Without thinking, Anne turns around and charges towards her window. As she reaches it, she flings it open and climbs out onto the roof. \n" +
                "\n" +
                "She doesn't realize that the only thing covering her is her panties until the cool night air hits her bare skin. Anne tries to cover her nakedness with her hands as she scrambles out the open window.\n" +
                "\n" +
                "Once outside, Anne hears the monster roar in anger. She can feel its hot breath on her back as she slides down the roof and lands on her feet, running towards the front of the house. \n" +
                "\n" +
                "Anne doesn't stop until she reaches the street corner, where she finally takes a moment to catch her breath. She looks back at her house and sees a dark figure emerge from her bedroom window.";

        StringProcessor outputProcessor = new StringProcessor();
        outputProcessor.addRule(s -> Pattern.compile("\\bAs Selkie, (\\w)").matcher(s).replaceAll(r -> r.group(1).toUpperCase()));
        outputProcessor.addRule(s -> Pattern.compile("\\bas Selkie, (\\w)").matcher(s).replaceAll(r -> r.group(1)));
        outputProcessor.addRule(s -> Pattern.compile("(?<=[.!?\\n])\"?[^.!?\\n]*(?![.!?\\n])$", Pattern.DOTALL & Pattern.MULTILINE).matcher(s).replaceAll(""));

        assertEquals(expected, outputProcessor.process(inputText));
    }
}
