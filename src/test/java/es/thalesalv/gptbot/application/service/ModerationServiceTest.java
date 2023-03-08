package es.thalesalv.gptbot.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.domain.exception.ModerationException;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.gptbot.testutils.ModerationBuilder;
import es.thalesalv.gptbot.testutils.PersonaBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
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

    @Test
    public void testModerationService_shouldNotTriggerFilter() {

        final Persona persona = PersonaBuilder.personaAbsoluteModeration();
        final String prompt = "This is a prompt";
        final MessageEventData eventData = PersonaBuilder.messageEventData();

        Mockito.when(openAIApiService.callModerationApi(any(ModerationRequest.class))).thenReturn(Mono.just(ModerationBuilder.moderationResponse()));
        Mockito.when(moderationService.moderate(prompt, eventData)).thenReturn(Mono.just(ModerationBuilder.moderationResponse()));

        StepVerifier.create(moderationService.moderate(prompt, eventData))
                .assertNext(r -> {
                    Assertions.assertEquals("text-davinci-003", r.getModel());
                    r.getModerationResult().get(0).getCategoryScores()
                            .entrySet().forEach(entry -> {
                                Assertions.assertEquals(0D, entry.getValue());
                            });
                }).verifyComplete();
    }

    @Test
    @Disabled
    public void testModerationService_shouldTriggerFilter_aboveThreshold() {

        final Persona persona = PersonaBuilder.personaAbsoluteModeration();
        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final String prompt = "This is a prompt";
        final Mono<ModerationResponse> response = Mono.just(ModerationBuilder.moderationResponseSexualFlag());
        final TextChannel textChannel = Mockito.mock(TextChannel.class);
        final Message message = Mockito.mock(Message.class);
        final User user = Mockito.mock(User.class);
        Mockito.when(jda.getTextChannelById(anyString())).thenReturn(textChannel);
        Mockito.when(jda.getUserById(anyString())).thenReturn(user);
        Mockito.when(textChannel.retrieveMessageById(anyString())).thenReturn(Mockito.mock(RestAction.class));
        Mockito.when(textChannel.retrieveMessageById(anyString()).complete()).thenReturn(message);
        Mockito.when(message.delete()).thenReturn(Mockito.mock(AuditableRestAction.class));
        Mockito.when(user.openPrivateChannel()).thenReturn(Mockito.mock(CacheRestAction.class));
        Mockito.when(user.openPrivateChannel().complete()).thenReturn(Mockito.mock(PrivateChannel.class));
        Mockito.when(user.openPrivateChannel().complete().sendMessage(anyString())).thenReturn(Mockito.mock(MessageCreateAction.class));
        Mockito.when(user.openPrivateChannel().complete().sendMessage(anyString()).complete()).thenReturn(Mockito.mock(Message.class));
        Mockito.when(openAIApiService.callModerationApi(any(ModerationRequest.class))).thenReturn(response);
        Mockito.when(moderationService.moderate(prompt, eventData)).thenReturn(response);

        StepVerifier.create(moderationService.moderate(prompt, eventData))
                .verifyError(ModerationException.class);
    }

    @Test
    @Disabled
    public void testModerationService_shouldTriggerFilter_absoluteModeration() {

        final Persona persona = PersonaBuilder.personaAbsoluteModeration();
        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final String prompt = "This is a prompt";
        final Mono<ModerationResponse> response = Mono.just(ModerationBuilder.moderationResponseSexualFlag());
        final TextChannel textChannel = Mockito.mock(TextChannel.class);
        final Message message = Mockito.mock(Message.class);
        final User user = Mockito.mock(User.class);
        Mockito.when(jda.getTextChannelById(anyString())).thenReturn(textChannel);
        Mockito.when(jda.getUserById(anyString())).thenReturn(user);
        Mockito.when(textChannel.retrieveMessageById(anyString())).thenReturn(Mockito.mock(RestAction.class));
        Mockito.when(textChannel.retrieveMessageById(anyString()).complete()).thenReturn(message);
        Mockito.when(message.delete()).thenReturn(Mockito.mock(AuditableRestAction.class));
        Mockito.when(user.openPrivateChannel()).thenReturn(Mockito.mock(CacheRestAction.class));
        Mockito.when(user.openPrivateChannel().complete()).thenReturn(Mockito.mock(PrivateChannel.class));
        Mockito.when(user.openPrivateChannel().complete().sendMessage(anyString())).thenReturn(Mockito.mock(MessageCreateAction.class));
        Mockito.when(user.openPrivateChannel().complete().sendMessage(anyString()).complete()).thenReturn(Mockito.mock(Message.class));
        Mockito.when(openAIApiService.callModerationApi(any())).thenReturn(response);
        Mockito.when(moderationService.moderate(prompt, eventData)).thenReturn(response);

        StepVerifier.create(moderationService.moderate(prompt, eventData))
                .verifyError(ModerationException.class);
    }
}
