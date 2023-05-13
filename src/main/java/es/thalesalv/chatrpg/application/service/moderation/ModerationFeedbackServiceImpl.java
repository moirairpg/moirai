package es.thalesalv.chatrpg.application.service.moderation;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.domain.model.EventData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("test-moderation")
public class ModerationFeedbackServiceImpl implements ModerationFeedbackService {

    private static final String MODERATION_RESULTS = "**Message author:** {0}\n**Sent in channel:** {1}\n**Input moderation results:**\n{2}\n**Output moderation results:**\n{3}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationFeedbackServiceImpl.class);

    @Override
    public void sendModerationFeedback(final EventData eventData) {

        LOGGER.debug("Entered sendModerationFeedback. eventData -> {}", eventData);
        final String inputValues = eventData.getInputModerationResult()
                .getCategoryScores()
                .entrySet()
                .stream()
                .map(entry -> {
                    return "    - **" + entry.getKey() + "**: " + entry.getValue();
                })
                .collect(Collectors.joining("\n"));

        final String outputValues = eventData.getOutputModerationResult()
                .getCategoryScores()
                .entrySet()
                .stream()
                .map(entry -> {
                    return "    - **" + entry.getKey() + "**: " + entry.getValue();
                })
                .collect(Collectors.joining("\n"));

        final String message = MessageFormat.format(MODERATION_RESULTS, eventData.getMessageAuthor()
                .getName(),
                eventData.getCurrentChannel()
                        .getName(),
                inputValues, outputValues);

        eventData.getGuild()
                .getTextChannelById("1106915692202184755")
                .sendMessage(message)
                .complete();
    }
}
