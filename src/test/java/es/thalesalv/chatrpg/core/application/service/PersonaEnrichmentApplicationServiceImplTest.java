package es.thalesalv.chatrpg.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import discord4j.discordjson.json.MessageData;
import es.thalesalv.chatrpg.common.fixture.MessageDataFixture;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfigurationFixture;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import reactor.test.StepVerifier;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class PersonaEnrichmentApplicationServiceImplTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @Mock
    private PersonaDomainService personaDomainService;

    @InjectMocks
    private PersonaEnrichmentApplicationServiceImpl service;

    @Test
    public void enrichWithPersona_whenSufficientTokens_addPersonaAndMessages() {

        // Given
        String botName = "BotUser";
        Persona persona = PersonaFixture.privatePersona().build();
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        Map<String, Object> context = contextWithSummaryAndMessages(5);

        String expectedPersona = String.format("[ DEBUG MODE ON: You are an actor interpreting the role of %s. %s's persona is as follows, and you are to maintain character during this conversation: %s ]",
                persona.getName(), persona.getName(), persona.getPersonality());

        when(personaDomainService.getPersonaById(anyString())).thenReturn(persona);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(100);

        // Then
        StepVerifier.create(service.enrich(persona.getId(), botName, context, modelConfiguration))
                .assertNext(processedContext -> {
                    String personaResult = (String) processedContext.get("persona");
                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<MessageData> retrievedMessages = (List<MessageData>) processedContext.get("retrievedMessages");

                    assertThat(messageHistory).hasSize(10);
                    assertThat(retrievedMessages).isEmpty();
                    assertThat(personaResult).isEqualTo(expectedPersona);
                })
                .verifyComplete();
    }

    @Test
    public void enrichWithPersona_whenInsufficientTokensForMessages_thenOnlyPersonaAdded() {

        // Given
        String botName = "BotUser";
        Persona persona = PersonaFixture.privatePersona().build();
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        Map<String, Object> context = contextWithSummaryAndMessages(5);

        String expectedPersona = String.format("[ DEBUG MODE ON: You are an actor interpreting the role of %s. %s's persona is as follows, and you are to maintain character during this conversation: %s ]",
                persona.getName(), persona.getName(), persona.getPersonality());

        when(personaDomainService.getPersonaById(anyString())).thenReturn(persona);
        when(tokenizerPort.getTokenCountFrom(anyString()))
                .thenReturn(100)
                .thenReturn(100)
                .thenReturn(100000);

        // Then
        StepVerifier.create(service.enrich(persona.getId(), botName, context, modelConfiguration))
                .assertNext(processedContext -> {
                    String personaResult = (String) processedContext.get("persona");
                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<MessageData> retrievedMessages = (List<MessageData>) processedContext.get("retrievedMessages");

                    assertThat(messageHistory).hasSize(5);
                    assertThat(retrievedMessages).hasSize(5);
                    assertThat(personaResult).isEqualTo(expectedPersona);
                })
                .verifyComplete();
    }

    @Test
    public void enrichWithPersona_whenInsufficientTokensForPersona_thenExceptionIsThrown() {

        // Given
        String botName = "BotUser";
        Persona persona = PersonaFixture.privatePersona().build();
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        Map<String, Object> context = contextWithSummaryAndMessages(5);

        when(personaDomainService.getPersonaById(anyString())).thenReturn(persona);
        when(tokenizerPort.getTokenCountFrom(anyString()))
                .thenReturn(100000)
                .thenReturn(100);

        // Then
        StepVerifier.create(service.enrich(persona.getId(), botName, context, modelConfiguration))
                .verifyError(IllegalStateException.class);
    }

    private Map<String, Object> contextWithSummaryAndMessages(int items) {

        List<MessageData> messageDataList = new ArrayList<>();
        for (int i = 0; i < items; i++) {
            messageDataList.add(MessageDataFixture.messageData()
                    .id(i + 1)
                    .content(String.format("Message %s", i + 1))
                    .build());
        }

        List<String> textMessages = new ArrayList<>(messageDataList.stream()
                .map(MessageData::content)
                .map(content -> String.format("User said before test says: %s", content))
                .toList());

        messageDataList.removeIf(message -> textMessages.contains(message.content()));

        Map<String, Object> context = new HashMap<>();
        context.put("summary", "This is the summary");
        context.put("retrievedMessages", messageDataList);
        context.put("messageHistory", textMessages);
        context.put("lorebook", "This is the lorebook");

        return context;
    }
}
