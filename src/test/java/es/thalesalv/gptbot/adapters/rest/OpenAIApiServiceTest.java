package es.thalesalv.gptbot.adapters.rest;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.errorhandling.CommonErrorHandler;
import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.exception.OpenAiApiException;
import es.thalesalv.gptbot.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.gptbot.testutils.ModerationBuilder;
import es.thalesalv.gptbot.testutils.OpenAiApiBuilder;
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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class OpenAIApiServiceTest {

    @Mock
    private JDA jda;

    @Mock
    private ContextDatastore contextDatastore;

    private CommonErrorHandler commonErrorHandler;
    private OpenAIApiService openAiApiService;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start(3434);
    }

    @BeforeEach
    void beforeEach() {

        commonErrorHandler = new CommonErrorHandler(jda);
        openAiApiService = new OpenAIApiService("http://" + mockWebServer.getHostName() + ":8080", WebClient.builder(), contextDatastore, commonErrorHandler);
        ReflectionTestUtils.setField(openAiApiService, "completionsUri", "/");
        ReflectionTestUtils.setField(openAiApiService, "chatCompletionsUri", "/");
        ReflectionTestUtils.setField(openAiApiService, "moderationUri", "/");
        ReflectionTestUtils.setField(openAiApiService, "openAiToken", "42342423545");
    }

    @AfterAll
    static void afterAll() throws IOException {

        mockWebServer.shutdown();
    }

    @Test
    public void testCompletionsApi_shouldWork() throws JsonProcessingException {

        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final GptResponse response = OpenAiApiBuilder.buildGptResponse();

        Mockito.when(contextDatastore.getMessageEventData()).thenReturn(eventData);

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(openAiApiService.callGptApi(request, eventData))
                .assertNext(resp -> {
                    Assertions.assertNotNull(resp);
                    Assertions.assertNull(resp.getError());
                    Assertions.assertEquals("text-davinci-003", resp.getModel());
                    Assertions.assertEquals("AI response text", resp.getChoices().get(0).getText());
                }).verifyComplete();
    }

    @Test
    public void testCompletionsApi_shouldThrowError_emptyResponse() throws JsonProcessingException {

        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final GptResponse response = OpenAiApiBuilder.buildGptResponseEmptyText();

        Mockito.when(contextDatastore.getMessageEventData()).thenReturn(eventData);

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Mono<GptResponse> underTest = openAiApiService.callGptApi(request, eventData);
        StepVerifier.create(underTest)
                .assertNext(resp -> {
                    Assertions.assertNotNull(resp);
                    Assertions.assertNull(resp.getError());
                    Assertions.assertEquals("text-davinci-003", resp.getModel());
                    Assertions.assertNull(resp.getChoices().get(0).getText());
                }).verifyComplete();
    }

    @Test
    public void testCompletionsApi_shouldThrowError_error4xx() throws JsonProcessingException {

        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final GptResponse response = OpenAiApiBuilder.buildGptResponse4xx();

        Mockito.when(contextDatastore.getMessageEventData()).thenReturn(eventData);
    
        final Message message = Mockito.mock(Message.class);
        Mockito.when(jda.getTextChannelById(Mockito.anyString())).thenReturn(Mockito.mock(TextChannel.class));
        Mockito.when(jda.getTextChannelById(Mockito.anyString())
                .retrieveMessageById(Mockito.anyString()))
                .thenReturn(Mockito.mock(RestAction.class));
    
        Mockito.when(jda.getTextChannelById(Mockito.anyString())
                .retrieveMessageById(Mockito.anyString()).complete())
                .thenReturn(message);
    
        Mockito.when(jda.getUserById(Mockito.anyString()))
                .thenReturn(Mockito.mock(User.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel()).thenReturn(Mockito.mock(CacheRestAction.class));
    
        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()).thenReturn(Mockito.mock(PrivateChannel.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()).complete())
                .thenReturn(message);

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()).complete())
                .thenReturn(message);

        Mockito.when(message.delete()).thenReturn(Mockito.mock(AuditableRestAction.class));

        mockWebServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(openAiApiService.callGptApi(request, eventData))
                .verifyError(OpenAiApiException.class);
    }

    @Test
    public void testCompletionsApi_shouldThrowError_apiError() throws JsonProcessingException {

        final MessageEventData eventData = PersonaBuilder.messageEventData();
        final Gpt3Request request = OpenAiApiBuilder.buildGpt3Request();
        final GptResponse response = OpenAiApiBuilder.buildGptResponse4xx();
        final ResponseSpec responseSpec = Mockito.mock(ResponseSpec.class);

        Mockito.when(contextDatastore.getMessageEventData()).thenReturn(eventData);

        final Message message = Mockito.mock(Message.class);
        Mockito.when(jda.getTextChannelById(Mockito.anyString())).thenReturn(Mockito.mock(TextChannel.class));
        Mockito.when(jda.getTextChannelById(Mockito.anyString())
                .retrieveMessageById(Mockito.anyString()))
                .thenReturn(Mockito.mock(RestAction.class));
    
        Mockito.when(jda.getTextChannelById(Mockito.anyString())
                .retrieveMessageById(Mockito.anyString()).complete())
                .thenReturn(message);
    
        Mockito.when(jda.getUserById(Mockito.anyString()))
                .thenReturn(Mockito.mock(User.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel()).thenReturn(Mockito.mock(CacheRestAction.class));
    
        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()).thenReturn(Mockito.mock(PrivateChannel.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()))
                .thenReturn(Mockito.mock(MessageCreateAction.class));

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()).complete())
                .thenReturn(message);

        Mockito.when(jda.getUserById(Mockito.anyString())
                .openPrivateChannel().complete()
                .sendMessage(Mockito.anyString()).complete())
                .thenReturn(message);

        Mockito.when(message.delete()).thenReturn(Mockito.mock(AuditableRestAction.class));

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Mono<GptResponse> underTest = openAiApiService.callGptApi(request, eventData);
        StepVerifier.create(underTest)
                .expectErrorSatisfies(t -> {
                    Assertions.assertTrue(t instanceof ErrorBotResponseException);
                    Assertions.assertTrue(!t.getMessage().isBlank());
                }).verify();
    }

    @Test
    public void testModerationApi_shouldSucceed() throws JsonProcessingException {

        final ModerationResponse response = ModerationBuilder.moderationResponse();
        final ModerationRequest request = ModerationBuilder.buildRequest();

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Mono<ModerationResponse> underTest = openAiApiService.callModerationApi(request);
        StepVerifier.create(underTest)
                .assertNext(resp -> {
                    Assertions.assertEquals("text-davinci-003", resp.getModel());
                    Assertions.assertFalse(resp.getModerationResult().get(0).getFlagged());
                }).verifyComplete();
    }

    @Test
    public void testModerationApi_shouldHaveContentFlag() throws JsonProcessingException {

        final ModerationResponse response = ModerationBuilder.moderationResponseSexualFlag();
        final ModerationRequest request = ModerationBuilder.buildRequest();

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        Mono<ModerationResponse> underTest = openAiApiService.callModerationApi(request);
        StepVerifier.create(underTest)
                .assertNext(resp -> {
                    Assertions.assertEquals("text-davinci-003", resp.getModel());
                    Assertions.assertTrue(resp.getModerationResult().get(0).getFlagged());
                }).verifyComplete();
    }
}
