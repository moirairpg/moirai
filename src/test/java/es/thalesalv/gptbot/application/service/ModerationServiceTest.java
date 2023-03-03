package es.thalesalv.gptbot.application.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.gptbot.testutils.ModerationResponseBuilder;
import es.thalesalv.gptbot.testutils.PersonaBuilder;
import net.dv8tion.jda.api.JDA;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class ModerationServiceTest {

    @Mock
    private JDA jda;

    @Mock
    private ContextDatastore contextDatastore;

    @Mock
    private OpenAIApiService openAIApiService;

    @InjectMocks
    private ModerationService moderationService;

    @BeforeEach
    public void before() {

        Mockito.when(contextDatastore.getMessageEventData()).thenReturn(PersonaBuilder.messageEventData());
        Mockito.when(contextDatastore.getPersona()).thenReturn(PersonaBuilder.persona());
    }

    @Test
    public void testModerationService_shouldNotTriggerFilter() {

        final Mono<ModerationResponse> response = Mono.just(ModerationResponseBuilder.moderationResponse());
        final String prompt = "This is a prompt";

        Mockito.when(openAIApiService.callModerationApi(Mockito.any())).thenReturn(response);
        Mockito.when(moderationService.moderate(prompt)).thenReturn(response);

        StepVerifier.create(moderationService.moderate(prompt))
                .assertNext(r -> {
                    Assertions.assertEquals("text-davinci-003", r.getModel());
                    r.getModerationResult().get(0).getCategoryScores()
                            .entrySet().forEach(entry -> {
                                Assertions.assertEquals(0D, entry.getValue());
                            });
                }).verifyComplete();
    }
}
