package me.moirai.storyengine.core.domain.character;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class PlayerCharacterTest {

    @Test
    public void shouldCreateInstanceWhenDataIsValid() {

        // given
        var builder = PlayerCharacter.builder()
                .name("Volin Habar")
                .playerId(1111L)
                .personality("Brave, honorable and disciplined.")
                .physicalDescription("A tall warrior with long black hair.")
                .background("Raised in a cliffside monastery.")
                .characterClass(CharacterClass.PALADIN)
                .attributes(PlayerCharacterFixture.sampleAttributeAllocation())
                .skills(PlayerCharacterFixture.sampleSkillAllocation())
                .signatureSkill(PlayerCharacterFixture.sampleSignatureAllocation());

        // when
        var character = builder.build();

        // then
        assertThat(character).isNotNull();
        assertThat(character.getPublicId()).isNotNull();
        assertThat(character.getName()).isEqualTo("Volin Habar");
        assertThat(character.getPlayerId()).isEqualTo(1111L);
        assertThat(character.getPersonality()).isEqualTo("Brave, honorable and disciplined.");
        assertThat(character.getPhysicalDescription()).isEqualTo("A tall warrior with long black hair.");
        assertThat(character.getBackground()).isEqualTo("Raised in a cliffside monastery.");
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.PALADIN);
    }

    @Test
    public void shouldCreateInstanceWhenAllSixPointsAreDistributedWithinTheCap() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter();

        // when
        var character = builder.build();

        // then
        assertThat(character.getAttributeLevels()).isNotNull();
        assertThat(character.getAttributeLevels().strength()).isEqualTo(3);
        assertThat(character.getAttributeLevels().agility()).isZero();
        assertThat(character.getAttributeLevels().vigor()).isEqualTo(2);
        assertThat(character.getAttributeLevels().intelligence()).isZero();
        assertThat(character.getAttributeLevels().awareness()).isZero();
        assertThat(character.getAttributeLevels().charisma()).isEqualTo(1);
    }

    @Test
    public void shouldThrowExceptionWhenAttributesAreMissing() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().attributes(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenAnAttributeHasNoLevel() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        allocation.remove(CharacterAttribute.CHARISMA);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().attributes(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenAnAttributeExceedsTheCreationCap() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        allocation.put(CharacterAttribute.STRENGTH, 4);
        allocation.put(CharacterAttribute.VIGOR, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().attributes(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenTotalPointsExceedTheBudget() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        allocation.put(CharacterAttribute.AGILITY, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().attributes(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenDistributionIsIncomplete() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        allocation.put(CharacterAttribute.CHARISMA, 0);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().attributes(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldCreateInstanceWhenAllFourSkillPointsAreDistributedWithinTheCap() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter();

        // when
        var character = builder.build();

        // then
        assertThat(character.getSkillLevels()).isNotNull();
        assertThat(character.getSkillLevels().persuasion()).isEqualTo(2);
        assertThat(character.getSkillLevels().endurance()).isEqualTo(2);
        assertThat(character.getSkillLevels().athletics()).isZero();
        assertThat(character.getSkillLevels().signature()).isEqualTo(1);
    }

    @Test
    public void shouldCreateInstanceWhenPointsAreSpentOnAnOffClassSkill() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.put(CharacterSkill.PERSUASION, 0);
        allocation.put(CharacterSkill.ENDURANCE, 0);
        allocation.put(CharacterSkill.STEALTH, 2);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(allocation);

        // when
        var character = builder.build();

        // then
        assertThat(character.getSkillLevels().stealth()).isEqualTo(2);
    }

    @Test
    public void shouldCreateInstanceWhenTheSignatureIsRaisedAtCreation() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.put(CharacterSkill.ENDURANCE, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter()
                .skills(allocation)
                .signatureSkill(Map.of(SignatureSkill.ZEAL, 2));

        // when
        var character = builder.build();

        // then
        assertThat(character.getSkillLevels().signature()).isEqualTo(2);
    }

    @Test
    public void shouldThrowExceptionWhenSkillsAreMissing() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenASkillHasNoLevel() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.remove(CharacterSkill.LORE);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenASkillExceedsTheCreationCap() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.put(CharacterSkill.PERSUASION, 3);
        allocation.put(CharacterSkill.ENDURANCE, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenSkillPointsExceedTheBudget() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.put(CharacterSkill.RESTORATION, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenSkillDistributionIsIncomplete() {

        // given
        var allocation = new HashMap<>(PlayerCharacterFixture.sampleSkillAllocation());
        allocation.put(CharacterSkill.ENDURANCE, 1);

        var builder = PlayerCharacterFixture.samplePlayerCharacter().skills(allocation);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenAnotherClassSignatureIsTrained() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter()
                .signatureSkill(Map.of(SignatureSkill.HEX, 1));

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenTheSignatureIsMissing() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().signatureSkill(Map.of());

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenTheSignatureIsBelowItsStartingLevel() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter()
                .signatureSkill(Map.of(SignatureSkill.ZEAL, 0));

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldUpdateNameWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateName("New Name");

        // then
        assertThat(character.getName()).isEqualTo("New Name");
    }

    @Test
    public void shouldUpdatePersonalityWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updatePersonality("New Personality");

        // then
        assertThat(character.getPersonality()).isEqualTo("New Personality");
    }

    @Test
    public void shouldUpdatePhysicalDescriptionWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updatePhysicalDescription("New Description");

        // then
        assertThat(character.getPhysicalDescription()).isEqualTo("New Description");
    }

    @Test
    public void shouldUpdateBackgroundWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateBackground("New Background");

        // then
        assertThat(character.getBackground()).isEqualTo("New Background");
    }

    @Test
    public void shouldThrowExceptionWhenNameIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateName(EMPTY));
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updatePersonality(EMPTY));
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class,
                () -> character.updatePhysicalDescription(EMPTY));
    }

    @Test
    public void shouldThrowExceptionWhenBackgroundIsUpdatedToBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateBackground(EMPTY));
    }

    @Test
    public void shouldUpdateImageKeyWhenNewValueIsProvided() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateImageKey("characters/volin.png");

        // then
        assertThat(character.getImageKey()).isEqualTo("characters/volin.png");
    }

    @Test
    public void shouldThrowExceptionWhenNameIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().name(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenNameIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().name(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPlayerIdIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().playerId(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().personality(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPersonalityIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().personality(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().physicalDescription(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenPhysicalDescriptionIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().physicalDescription(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenBackgroundIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().background(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenBackgroundIsEmpty() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().background(EMPTY);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenCharacterClassIsNull() {

        // given
        var builder = PlayerCharacterFixture.samplePlayerCharacter().characterClass(null);

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWhenNoFieldIsProvided() {

        // given
        var builder = PlayerCharacter.builder();

        // then
        assertThrows(BusinessRuleViolationException.class, builder::build);
    }

    @Test
    public void shouldRecordDeletedEventWhenCharacterIsDeleted() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        // when
        character.communicateCharacterDeleted();

        // then
        var events = character.drainEvents();

        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(PlayerCharacterDeletedEvent.class);

        var event = (PlayerCharacterDeletedEvent) events.getFirst();

        assertThat(event.getPlayerCharacterId()).isEqualTo(PlayerCharacterFixture.NUMERIC_ID);
        assertThat(event.getPlayerId()).isEqualTo(PlayerCharacterFixture.PLAYER_ID);
    }

    @Test
    public void shouldEmptyRecordedEventsWhenDrained() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.communicateCharacterDeleted();

        // when
        character.drainEvents();

        // then
        assertThat(character.drainEvents()).isEmpty();
    }

    @Test
    public void shouldJoinNameClassPersonalityDescriptionAndBackgroundWhenBuildingTheNarrativeDescription() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        var description = character.narrativeDescription();

        // then
        assertThat(description).isEqualTo(
                "Volin Habar: PALADIN; Brave, honorable and disciplined."
                        + "; A tall warrior with long black hair and a scar across his left cheek."
                        + "; Raised in a cliffside monastery, he took the oath after his village burned.");
    }

    @Test
    public void shouldReflectTheNewValueInTheNarrativeDescriptionWhenAFieldIsUpdated() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateName("Volin the Bold");

        // then
        assertThat(character.narrativeDescription()).startsWith("Volin the Bold: PALADIN;");
    }

    @Test
    public void shouldNotRaiseAnyEventWhenTheNameChanges() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        // when
        character.updateName("Volin the Bold");

        // then
        assertThat(character.getName()).isEqualTo("Volin the Bold");
        assertThat(character.drainEvents()).isEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenTheNewNameIsBlank() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateName(EMPTY));
    }

    @Test
    public void shouldReplaceClassAndSheetWhenTheSheetIsUpdated() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.updateSheet(
                CharacterClass.MAGE,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                PlayerCharacterFixture.skillAllocationFor(CharacterClass.MAGE),
                PlayerCharacterFixture.signatureAllocationFor(CharacterClass.MAGE));

        // then
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.MAGE);
        assertThat(character.getSkillLevels().destruction()).isEqualTo(2);
        assertThat(character.getSkillLevels().conjuration()).isEqualTo(2);
        assertThat(character.getSkillLevels().persuasion()).isZero();
        assertThat(character.getSkillLevels().signature()).isEqualTo(1);
    }

    @Test
    public void shouldReplaceTheSheetWhenTheClassIsUnchanged() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        var skills = new HashMap<>(PlayerCharacterFixture.skillAllocationFor(CharacterClass.PALADIN));
        skills.put(CharacterSkill.PERSUASION, 0);
        skills.put(CharacterSkill.ENDURANCE, 0);
        skills.put(CharacterSkill.RESTORATION, 2);
        skills.put(CharacterSkill.MELEE, 2);

        // when
        character.updateSheet(
                CharacterClass.PALADIN,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                skills,
                PlayerCharacterFixture.sampleSignatureAllocation());

        // then
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.PALADIN);
        assertThat(character.getSkillLevels().restoration()).isEqualTo(2);
        assertThat(character.getSkillLevels().melee()).isEqualTo(2);
        assertThat(character.getSkillLevels().persuasion()).isZero();
    }

    @Test
    public void shouldThrowExceptionWhenTheSheetIsUpdatedWithoutAClass() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                null,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                PlayerCharacterFixture.sampleSkillAllocation(),
                PlayerCharacterFixture.sampleSignatureAllocation()));
    }

    @Test
    public void shouldThrowExceptionWhenTheUpdatedSheetViolatesTheSkillBudget() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        var skills = new HashMap<>(PlayerCharacterFixture.skillAllocationFor(CharacterClass.MAGE));
        skills.put(CharacterSkill.LORE, 1);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                CharacterClass.MAGE,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                skills,
                PlayerCharacterFixture.signatureAllocationFor(CharacterClass.MAGE)));
    }

    @Test
    public void shouldThrowExceptionWhenTheUpdatedSheetTrainsAnotherClassSignature() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                CharacterClass.MAGE,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                PlayerCharacterFixture.skillAllocationFor(CharacterClass.MAGE),
                Map.of(SignatureSkill.HEX, 1)));
    }

    @Test
    public void shouldThrowExceptionWhenAClasslessCharacterIsValidatedForHavingAClass() {

        // given
        var character = new PlayerCharacter();

        // then
        assertThrows(BusinessRuleViolationException.class, character::validateHasClass);
    }

    @Test
    public void shouldNotThrowExceptionWhenACharacterWithAClassIsValidatedForHavingAClass() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.validateHasClass();

        // then
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.PALADIN);
    }

    @Test
    public void shouldLevelUpAndCarryTheRemainderWhenXpReachesTheThreshold() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        character.awardXp(95);

        // when
        character.awardXp(10);

        // then
        assertThat(character.getLevel()).isEqualTo(2);
        assertThat(character.getXp()).isEqualTo(5);
        assertThat(character.getUnspentAttributePoints()).isEqualTo(1);
        assertThat(character.getUnspentSkillPoints()).isEqualTo(2);
        assertThat(character.drainEvents()).hasSize(1);
    }

    @Test
    public void shouldStackBalancesAcrossLevelUps() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.awardXp(100);
        character.awardXp(100);

        // then
        assertThat(character.getLevel()).isEqualTo(3);
        assertThat(character.getUnspentAttributePoints()).isEqualTo(2);
        assertThat(character.getUnspentSkillPoints()).isEqualTo(4);
        assertThat(character.drainEvents()).hasSize(2);
    }

    @Test
    public void shouldNotLevelUpBelowTheThreshold() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        // when
        character.awardXp(99);

        // then
        assertThat(character.getLevel()).isEqualTo(1);
        assertThat(character.getXp()).isEqualTo(99);
        assertThat(character.drainEvents()).isEmpty();
    }

    @Test
    public void shouldConsumeUnspentPointsWhenTheSheetGrowsInValue() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        character.awardXp(200);

        var attributes = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        attributes.put(CharacterAttribute.STRENGTH, 4);

        // when
        character.updateSheet(
                CharacterClass.PALADIN,
                attributes,
                PlayerCharacterFixture.sampleSkillAllocation(),
                PlayerCharacterFixture.sampleSignatureAllocation());

        // then
        assertThat(character.getAttributeLevels().strength()).isEqualTo(4);
        assertThat(character.getUnspentAttributePoints()).isZero();
        assertThat(character.getUnspentSkillPoints()).isEqualTo(4);
    }

    @Test
    public void shouldRejectTheSheetWhenItLosesValue() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        var attributes = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        attributes.put(CharacterAttribute.STRENGTH, 2);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                CharacterClass.PALADIN,
                attributes,
                PlayerCharacterFixture.sampleSkillAllocation(),
                PlayerCharacterFixture.sampleSignatureAllocation()));
    }

    @Test
    public void shouldRejectTheSheetWhenGrowthExceedsTheBalances() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();

        var attributes = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        attributes.put(CharacterAttribute.STRENGTH, 4);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                CharacterClass.PALADIN,
                attributes,
                PlayerCharacterFixture.sampleSkillAllocation(),
                PlayerCharacterFixture.sampleSignatureAllocation()));
    }

    @Test
    public void shouldAcceptAnEqualValueRedistributionWithoutTouchingBalances() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        character.awardXp(100);

        // when
        character.updateSheet(
                CharacterClass.MAGE,
                PlayerCharacterFixture.sampleAttributeAllocation(),
                PlayerCharacterFixture.skillAllocationFor(CharacterClass.MAGE),
                PlayerCharacterFixture.signatureAllocationFor(CharacterClass.MAGE));

        // then
        assertThat(character.getCharacterClass()).isEqualTo(CharacterClass.MAGE);
        assertThat(character.getUnspentAttributePoints()).isEqualTo(1);
        assertThat(character.getUnspentSkillPoints()).isEqualTo(2);
    }

    @Test
    public void shouldRejectLevelsAboveTheAbsoluteCaps() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacter().build();
        character.awardXp(500);

        var attributes = new HashMap<>(PlayerCharacterFixture.sampleAttributeAllocation());
        attributes.put(CharacterAttribute.STRENGTH, 6);

        // then
        assertThrows(BusinessRuleViolationException.class, () -> character.updateSheet(
                CharacterClass.PALADIN,
                attributes,
                PlayerCharacterFixture.sampleSkillAllocation(),
                PlayerCharacterFixture.sampleSignatureAllocation()));
    }
}
