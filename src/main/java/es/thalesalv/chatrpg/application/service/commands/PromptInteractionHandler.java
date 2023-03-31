package es.thalesalv.chatrpg.application.service.commands;

import java.util.concurrent.TimeUnit;

import es.thalesalv.chatrpg.domain.enums.Intent;
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
public class PromptInteractionHandler implements DiscordInteractionHandler {

    private final ChannelEntityToDTO channelEntityToDTO;
    private final ContextDatastore contextDatastore;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final EventDataMapper eventDataMapper;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String USE_CASE = "UseCase";
    private static final String COMMAND_STRING = "prompt";
    private static final String MESSAGE_CONTENT = "message-content";
    private static final String GENERATE_OUTPUT = "generate-output";
    private static final String MODAL_ID = COMMAND_STRING + "-message-dmassist-modal";
    private static final String NO_CONFIG_ATTACHED = "No configuration is attached to channel.";
    private static final String GENERATION_INSTRUCTION = " Simply generate the message from where it stopped.\n";
    private static final String ERROR_GENERATING = "Error generating message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when generating the message. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(PromptInteractionHandler.class);

    @Override
    public void handleCommand(SlashCommandInteractionEvent event) {

        LOGGER.debug("handling {} command", COMMAND_STRING);
        try {
            event.deferReply();
            final MessageChannelUnion channel = event.getChannel();
            channelRepository.findById(event.getChannel()
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

        LOGGER.debug("handling {} modal", COMMAND_STRING);
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
            final String input = event.getValue(MESSAGE_CONTENT)
                    .getAsString();
            final String generateOutput = event.getValue(GENERATE_OUTPUT)
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

    private String formatInput(Intent intent, String prompt, SelfUser bot) {

        return Intent.RPG.equals(intent) ? bot.getAsMention() + prompt : prompt;
    }

    private Modal buildEditMessageModal() {

        LOGGER.debug("Building assisted prompt modal");
        final TextInput messageContent = TextInput.create(MESSAGE_CONTENT, "Message content", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setMaxLength(2000)
                .setRequired(true)
                .build();
        final TextInput lorebookEntryPlayer = TextInput
                .create(GENERATE_OUTPUT, "Generate output?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();
        return Modal.create(MODAL_ID, "Type prompt")
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
