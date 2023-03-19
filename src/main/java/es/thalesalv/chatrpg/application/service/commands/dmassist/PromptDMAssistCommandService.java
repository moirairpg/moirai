package es.thalesalv.chatrpg.application.service.commands.dmassist;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.ContextDatastore;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.domain.enums.AIModelEnum;
import es.thalesalv.chatrpg.domain.model.openai.dto.CommandEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@RequiredArgsConstructor
public class PromptDMAssistCommandService extends DiscordCommand {

    private final ChannelEntityListToDTOList channelEntityListToDTOList;
    private final ContextDatastore contextDatastore;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String GENERATION_INSTRUCTION = " Simply generate the message from where it stopped.\n";
    private static final String USE_CASE = "UseCase";
    private static final String ERROR_GENERATING = "Error generating message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when generating the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PromptDMAssistCommandService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for assisted prompr");
        try {
            event.deferReply();
            final MessageChannelUnion channel = event.getChannel();
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                    .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                    .findFirst()
                    .ifPresent(ch -> {
                        contextDatastore.setCommandEventData(CommandEventData.builder()
                                .channelConfig(ch.getChannelConfig())
                                .channel(channel)
                                .build());

                        event.replyModal(buildEditMessageModal()).queue();
                    });
        } catch (Exception e) {
            LOGGER.error("Error regenerating output", e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        LOGGER.debug("Received data of message for assisted prompt generation modal");
        try {
            event.deferReply();
            final Persona persona = contextDatastore.getCommandEventData().getChannelConfig().getPersona();
            final ModelSettings modelSettings = contextDatastore.getCommandEventData().getChannelConfig().getSettings().getModelSettings();

            final String input = event.getValue("message-content").getAsString();
            final String generateOutput = event.getValue("generate-output").getAsString();
            final SelfUser bot = event.getJDA().getSelfUser();
            final MessageChannelUnion channel = contextDatastore.getCommandEventData().getChannel();
            final Message message = channel.sendMessage(bot.getAsMention() + GENERATION_INSTRUCTION + input).complete();
            event.reply("Assisted prompt used").setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));

            if (generateOutput.equals("y")) {
                final MessageEventData messageEventData = messageEventDataTranslator.translate(event.getJDA()
                        .getSelfUser(), channel, contextDatastore.getCommandEventData().getChannelConfig(), message);

                final String completionType = AIModelEnum.findByInternalName(modelSettings.getModelName()).getCompletionType();
                final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);

                useCase.generateResponse(messageEventData, model);
            }

            message.editMessage(message.getContentRaw()
                    .replace(bot.getAsMention() + GENERATION_INSTRUCTION, StringUtils.EMPTY).trim()).complete();
        } catch (Exception e) {
            LOGGER.error(ERROR_GENERATING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true).queue();
        }
    }

    private Modal buildEditMessageModal() {

        LOGGER.debug("Building assisted prompt modal");
        final TextInput messageContent = TextInput
                .create("message-content", "Message content", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setMaxLength(2000)
                .setRequired(true)
                .build();

        final TextInput lorebookEntryPlayer = TextInput
                .create("generate-output", "Generate output?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create("prompt-message-dmassist-modal", "Type prompt")
                .addComponents(ActionRow.of(messageContent), ActionRow.of(lorebookEntryPlayer)).build();
    }
}
