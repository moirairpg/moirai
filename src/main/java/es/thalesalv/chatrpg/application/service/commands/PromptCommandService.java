package es.thalesalv.chatrpg.application.service.commands;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.EventDataMapper;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
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
public class PromptCommandService implements DiscordCommand {

    private static final String COMMAND_STRING = "prompt";
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ContextDatastore contextDatastore;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final EventDataMapper eventDataMapper;
    private static final String USE_CASE = "UseCase";
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String NO_CONFIG_ATTACHED = "No configuration is attached to channel.";
    private static final String GENERATION_INSTRUCTION = " Simply generate the message from where it stopped.\n";
    private static final String ERROR_GENERATING = "Error generating message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when generating the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PromptCommandService.class);

    @Override
    public void handleCommand(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for assisted prompt");
        try {
            event.deferReply();
            final MessageChannelUnion channel = event.getChannel();
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(ch -> {
                        contextDatastore.setEventData(EventData.builder()
                                .channelDefinitions(ch)
                                .currentChannel(channel)
                                .build());
                        event.replyModal(buildEditMessageModal())
                                .queue();
                        return ch;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.debug(NO_CONFIG_ATTACHED);
            event.reply(NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error("Error regenerating output", e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    @Override
    public void handleModal(ModalInteractionEvent event) {

        LOGGER.debug("Received data of message for assisted prompt generation modal");
        try {
            event.deferReply();
            final MessageChannelUnion channel = contextDatastore.getEventData()
                    .getCurrentChannel();
            final Channel channelDefinition = contextDatastore.getEventData()
                    .getChannelDefinitions();
            final Persona persona = channelDefinition.getChannelConfig()
                    .getPersona();
            final ModelSettings modelSettings = channelDefinition.getChannelConfig()
                    .getSettings()
                    .getModelSettings();
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            final String input = event.getValue("message-content")
                    .getAsString();
            final String generateOutput = event.getValue("generate-output")
                    .getAsString();
            final String formattedInput = formatInput(persona.getIntent(), GENERATION_INSTRUCTION + input, bot);
            final Message message = channel.sendMessage(formattedInput)
                    .complete();
            event.reply("Assisted prompt used.")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(1, TimeUnit.MILLISECONDS));
            if (generateOutput.equals("y")) {
                final EventData eventData = eventDataMapper.translate(bot, channel, channelDefinition, message);
                final String completionType = AIModel.findByInternalName(modelSettings.getModelName())
                        .getCompletionType();
                final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                useCase.generateResponse(eventData, model);
            }
            message.editMessage(message.getContentRaw()
                    .replace(bot.getAsMention() + GENERATION_INSTRUCTION, StringUtils.EMPTY)
                    .trim())
                    .complete();
        } catch (Exception e) {
            LOGGER.error(ERROR_GENERATING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private String formatInput(String intent, String prompt, SelfUser bot) {

        return "rpg".equals(intent) ? bot.getAsMention() + prompt : prompt;
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
                .addComponents(ActionRow.of(messageContent), ActionRow.of(lorebookEntryPlayer))
                .build();
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for bot prompt");
        return Commands.slash(COMMAND_STRING,
                "Prompts as the bot's persona and allows for a generation in addition to the provided prompt.");
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
