package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.SayCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@SuppressWarnings("unchecked")
public class ModalListenerTest extends AbstractDiscordTest {

    @Mock
    private UseCaseRunner useCaseRunner;

    @InjectMocks
    private ModalListener listener;

    @Test
    public void sayModal_whenCommandCalled_thenSendInputToChannel() {

        // Given
        String channelId = "CHID";
        String modalName = "sayAsBot";
        String messageContent = "Message that will be sent as the bot";

        InteractionHook interactionHook = mock(InteractionHook.class);
        ReplyCallbackAction sendMessageCallback = mock(ReplyCallbackAction.class);
        ModalInteractionEvent event = mock(ModalInteractionEvent.class);
        ModalMapping modalMapping = mock(ModalMapping.class);
        WebhookMessageEditAction<Message> editNotificationAction = mock(WebhookMessageEditAction.class);

        ArgumentCaptor<SayCommand> useCaseCaptor = ArgumentCaptor.forClass(SayCommand.class);

        when(event.getModalId()).thenReturn(modalName);
        when(event.getChannel()).thenReturn(channelUnion);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(textChannel.getId()).thenReturn(channelId);
        when(event.reply(anyString())).thenReturn(sendMessageCallback);
        when(sendMessageCallback.setEphemeral(anyBoolean())).thenReturn(sendMessageCallback);
        when(sendMessageCallback.complete()).thenReturn(interactionHook);
        when(event.getValue(anyString())).thenReturn(modalMapping);
        when(modalMapping.getAsString()).thenReturn(messageContent);
        when(interactionHook.editOriginal(anyString())).thenReturn(editNotificationAction);
        when(editNotificationAction.complete()).thenReturn(message);

        // When
        listener.onModalInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(useCaseCaptor.capture());

        SayCommand useCase = useCaseCaptor.getValue();

        assertThat(useCase.getMessageContent()).isEqualTo(messageContent);
        assertThat(useCase.getChannelId()).isEqualTo(channelId);
    }
}
