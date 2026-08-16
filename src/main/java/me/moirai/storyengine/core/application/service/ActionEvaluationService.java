package me.moirai.storyengine.core.application.service;

import static me.moirai.storyengine.common.util.DefaultStringProcessors.addChatPrefix;

import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.moirai.storyengine.common.dto.DiceRollSummary;
import me.moirai.storyengine.common.enums.ActionOutcome;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.domain.character.AttributeLevels;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationResult;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessagePort;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@Service
public class ActionEvaluationService {

    private static final Logger LOG = LoggerFactory.getLogger(ActionEvaluationService.class);

    private static final int RECENT_HISTORY_SIZE = 10;
    private static final String EVALUATION_FAILED = "Action evaluation failed for adventure {}";

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final ActionEvaluationPort actionEvaluationPort;
    private final AdventureMessagePort adventureMessagePort;
    private final RandomGenerator randomGenerator;

    public ActionEvaluationService(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            PlayerCharacterRepository playerCharacterRepository,
            ActionEvaluationPort actionEvaluationPort,
            AdventureMessagePort adventureMessagePort,
            RandomGenerator randomGenerator) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.playerCharacterRepository = playerCharacterRepository;
        this.actionEvaluationPort = actionEvaluationPort;
        this.adventureMessagePort = adventureMessagePort;
        this.randomGenerator = randomGenerator;
    }

    public String evaluateLatestPlayerAction(UUID adventurePublicId) {

        try {
            var adventure = adventureRepository.findByPublicId(adventurePublicId)
                    .orElseThrow(() -> new NotFoundException("Adventure not found"));

            if (!adventure.isRpgMechanicsEnabled()) {
                return null;
            }

            var history = messageRepository.findAllActiveByAdventureId(adventure.getId());

            if (history.isEmpty() || history.getLast().getRole() != MessageAuthorRole.USER) {
                return null;
            }

            var evaluation = actionEvaluationPort.evaluateAction(new ActionEvaluationRequest(
                    MessagePrompt.ACTION_EVALUATOR.getText(),
                    toRecentContext(history)));

            if (evaluation.verdict() == ActionVerdict.NO_CHECK) {
                return null;
            }

            var playerMessage = history.getLast();

            if (evaluation.verdict() == ActionVerdict.IMPOSSIBLE) {
                return MessagePrompt.ACTION_OUTCOME_IMPOSSIBLE.formatted(
                        playerMessage.getAuthorCharacterName(), resolveActionTarget(evaluation));
            }

            var character = playerCharacterRepository.findById(playerMessage.getAuthorCharacterId())
                    .orElseThrow(() -> new NotFoundException("Acting character not found"));

            var modifier = calculateRollModifier(character, evaluation.attribute(), evaluation.skill());
            var naturalRoll = randomGenerator.nextInt(1, 21);
            var total = naturalRoll + modifier;
            var dc = evaluation.difficulty().getDc();
            var outcome = determineOutcome(naturalRoll, total, dc);

            adventureMessagePort.send(adventurePublicId, AdventureMessageUpdate.diceRolled(new DiceRollSummary(
                    playerMessage.getAuthorCharacterName(),
                    Functions.mapOrNull(evaluation.attribute(), Enum::name),
                    evaluation.skill(),
                    evaluation.difficulty(),
                    dc,
                    naturalRoll,
                    modifier,
                    total,
                    outcome)));

            return formatOutcomeLine(outcome, playerMessage.getAuthorCharacterName(), resolveActionTarget(evaluation));

        } catch (RuntimeException e) {
            LOG.error(EVALUATION_FAILED, adventurePublicId, e);
            return null;
        }
    }

    private List<ChatMessage> toRecentContext(List<Message> history) {

        return history.stream()
                .skip(Math.max(0, history.size() - RECENT_HISTORY_SIZE))
                .map(message -> message.getRole() == MessageAuthorRole.USER
                        ? ChatMessage.asUser(addChatPrefix(message.getAuthorCharacterName()).apply(message.getContent()))
                        : ChatMessage.asAssistant(message.getContent()))
                .toList();
    }

    private int calculateRollModifier(PlayerCharacter character, CharacterAttribute attribute, String skillName) {

        var attributes = character.getAttributeLevels();
        var skills = character.getSkillLevels();

        if (skillName != null && EnumUtils.isValidEnum(CharacterSkill.class, skillName)) {
            var skill = CharacterSkill.valueOf(skillName);
            return getAttributeLevel(attributes, skill.getAttribute()) + skills.asMap().get(skill);
        }

        if (skillName != null && EnumUtils.isValidEnum(SignatureSkill.class, skillName)) {
            var signature = SignatureSkill.valueOf(skillName);
            var signatureLevel = character.getCharacterClass().getSignature() == signature
                    ? skills.signature()
                    : 0;

            return getAttributeLevel(attributes, signature.getAttribute()) + signatureLevel;
        }

        if (attribute != null) {
            return getAttributeLevel(attributes, attribute);
        }

        return 0;
    }

    private int getAttributeLevel(AttributeLevels attributes, CharacterAttribute attribute) {
        return attributes.asMap().get(attribute);
    }

    private String formatOutcomeLine(ActionOutcome outcome, String characterName, String actionTarget) {

        var template = switch (outcome) {
            case CRITICAL_FAILURE -> MessagePrompt.ACTION_OUTCOME_CRITICAL_FAILURE;
            case FAILURE -> MessagePrompt.ACTION_OUTCOME_FAILURE;
            case SUCCESS -> MessagePrompt.ACTION_OUTCOME_SUCCESS;
            case CRITICAL_SUCCESS -> MessagePrompt.ACTION_OUTCOME_CRITICAL_SUCCESS;
        };

        return template.formatted(characterName, actionTarget);
    }

    private String resolveActionTarget(ActionEvaluationResult evaluation) {

        if (evaluation.skill() != null) {
            return evaluation.skill();
        }

        return evaluation.attribute() != null ? evaluation.attribute().name() : "the action";
    }

    private ActionOutcome determineOutcome(int naturalRoll, int total, int dc) {

        if (naturalRoll == 1) {
            return ActionOutcome.CRITICAL_FAILURE;
        }

        if (naturalRoll == 20) {
            return ActionOutcome.CRITICAL_SUCCESS;
        }

        return total >= dc ? ActionOutcome.SUCCESS : ActionOutcome.FAILURE;
    }
}
