package me.moirai.storyengine.core.domain.character;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.uuid.Generators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import me.moirai.storyengine.common.domain.Asset;
import me.moirai.storyengine.common.domain.DomainEvent;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.rules.CharacterSheetRules;

@Entity
@Table(name = "player_character")
public class PlayerCharacter extends Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id")
    private UUID publicId;

    @Column(name = "name")
    private String name;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "personality")
    private String personality;

    @Column(name = "physical_description")
    private String physicalDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_class")
    private CharacterClass characterClass;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private AttributeLevels attributeLevels;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skills", columnDefinition = "jsonb")
    private SkillLevels skillLevels;

    @Column(name = "xp")
    private int xp;

    @Column(name = "level")
    private int level = 1;

    @Column(name = "unspent_attribute_points")
    private int unspentAttributePoints;

    @Column(name = "unspent_skill_points")
    private int unspentSkillPoints;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "ui_image_position_x")
    private Double uiImagePositionX;

    @Column(name = "ui_image_position_y")
    private Double uiImagePositionY;

    @Transient
    private List<DomainEvent> domainEvents = new ArrayList<>();

    protected PlayerCharacter() {
        super();
    }

    private PlayerCharacter(Builder builder) {

        super();

        this.publicId = Generators.timeBasedEpochGenerator().generate();
        this.name = builder.name;
        this.playerId = builder.playerId;
        this.personality = builder.personality;
        this.physicalDescription = builder.physicalDescription;
        this.characterClass = builder.characterClass;
        this.attributeLevels = AttributeLevels.of(builder.attributes);
        this.skillLevels = SkillLevels.of(builder.skills,
                builder.signatureSkill.get(builder.characterClass.getSignature()));
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<DomainEvent> drainEvents() {
        var snapshot = List.copyOf(domainEvents);
        domainEvents.clear();
        return snapshot;
    }

    public void communicateCharacterDeleted() {
        domainEvents.add(new PlayerCharacterDeletedEvent(this.id, this.playerId, this.publicId, this.imageKey));
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getName() {
        return name;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getPersonality() {
        return personality;
    }

    public String getPhysicalDescription() {
        return physicalDescription;
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public AttributeLevels getAttributeLevels() {
        return attributeLevels;
    }

    public SkillLevels getSkillLevels() {
        return skillLevels;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getUnspentAttributePoints() {
        return unspentAttributePoints;
    }

    public int getUnspentSkillPoints() {
        return unspentSkillPoints;
    }

    public void awardXp(int amount) {

        this.xp += amount;

        while (this.xp >= CharacterSheetRules.LEVEL_UP_XP_THRESHOLD) {
            this.xp -= CharacterSheetRules.LEVEL_UP_XP_THRESHOLD;
            this.level++;
            this.unspentAttributePoints += CharacterSheetRules.ATTRIBUTE_POINTS_PER_LEVEL;
            this.unspentSkillPoints += CharacterSheetRules.SKILL_POINTS_PER_LEVEL;

            domainEvents.add(new CharacterLeveledUpEvent(id, publicId, playerId, name, level));
        }
    }

    public boolean isFullyTrained() {

        var allAttributesMaxed = attributeLevels.asMap().values().stream()
                .allMatch(attributeLevel -> attributeLevel == CharacterSheetRules.ATTRIBUTE_MAX_LEVEL);

        var allSkillsMaxed = skillLevels.asMap().values().stream()
                .allMatch(skillLevel -> skillLevel == CharacterSheetRules.SKILL_MAX_LEVEL);

        return allAttributesMaxed && allSkillsMaxed
                && skillLevels.signature() == CharacterSheetRules.SKILL_MAX_LEVEL;
    }

    public String narrativeDescription() {
        return name + ": " + characterClass.name() + "; " + personality + "; " + physicalDescription;
    }

    public String getImageKey() {
        return imageKey;
    }

    public Double getUiImagePositionX() {
        return uiImagePositionX;
    }

    public Double getUiImagePositionY() {
        return uiImagePositionY;
    }

    public void updateName(String name) {

        if (isBlank(name)) {
            throw new BusinessRuleViolationException("Character name cannot be null or empty");
        }

        this.name = name;
    }

    public void updatePersonality(String personality) {

        if (isBlank(personality)) {
            throw new BusinessRuleViolationException("Character personality cannot be null or empty");
        }

        this.personality = personality;
    }

    public void updatePhysicalDescription(String physicalDescription) {

        if (isBlank(physicalDescription)) {
            throw new BusinessRuleViolationException("Character physical description cannot be null or empty");
        }

        this.physicalDescription = physicalDescription;
    }

    public void updateSheet(
            CharacterClass characterClass,
            Map<CharacterAttribute, Integer> attributes,
            Map<CharacterSkill, Integer> skills,
            Map<SignatureSkill, Integer> signatureSkill) {

        if (this.characterClass == null) {
            validateSheet(characterClass, attributes, skills, signatureSkill);

            this.characterClass = characterClass;
            this.attributeLevels = AttributeLevels.of(attributes);
            this.skillLevels = SkillLevels.of(skills, signatureSkill.get(characterClass.getSignature()));

            return;
        }

        validateSheetStructure(characterClass, attributes, skills, signatureSkill);

        var signatureLevel = signatureSkill.get(characterClass.getSignature());
        var newAttributeLevels = AttributeLevels.of(attributes);
        var newSkillLevels = SkillLevels.of(skills, signatureLevel);

        var attributeConsumed = calculateSpentAttributePoints(attributes)
                - calculateSpentAttributePoints(attributeLevels.asMap());
        var skillConsumed = calculateSpentSkillPoints(characterClass, skills, signatureLevel)
                - calculateSpentSkillPoints(this.characterClass, skillLevels.asMap(), skillLevels.signature());

        if (attributeConsumed < 0 || skillConsumed < 0) {
            throw new BusinessRuleViolationException("The sheet cannot lose points");
        }

        if (attributeConsumed > unspentAttributePoints || skillConsumed > unspentSkillPoints) {
            throw new BusinessRuleViolationException("Not enough unspent points");
        }

        this.unspentAttributePoints -= attributeConsumed;
        this.unspentSkillPoints -= skillConsumed;
        this.characterClass = characterClass;
        this.attributeLevels = newAttributeLevels;
        this.skillLevels = newSkillLevels;
    }

    public void validateHasClass() {

        if (characterClass == null) {
            throw new BusinessRuleViolationException("Character needs a class");
        }
    }

    public void updateImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public void updateUiImagePosition(Double uiImagePositionX, Double uiImagePositionY) {
        this.uiImagePositionX = uiImagePositionX;
        this.uiImagePositionY = uiImagePositionY;
    }

    public String generateImageKey() {

        var imageId = Generators.timeBasedEpochGenerator().generate();
        this.imageKey = "characters/" + this.publicId + "/" + imageId + ".png";

        return this.imageKey;
    }

    private static void validateSheetStructure(
            CharacterClass characterClass,
            Map<CharacterAttribute, Integer> attributes,
            Map<CharacterSkill, Integer> skills,
            Map<SignatureSkill, Integer> signatureSkill) {

        if (characterClass == null) {
            throw new BusinessRuleViolationException("Character class cannot be null");
        }

        if (attributes == null) {
            throw new BusinessRuleViolationException("Character must have attribute levels");
        }

        var hasMissingAttribute = Arrays.stream(CharacterAttribute.values())
                .anyMatch(attribute -> attributes.get(attribute) == null);

        if (hasMissingAttribute) {
            throw new BusinessRuleViolationException("All six attributes must receive a level");
        }

        if (skills == null) {
            throw new BusinessRuleViolationException("Character must have skill levels");
        }

        var hasMissingSkill = Arrays.stream(CharacterSkill.values())
                .anyMatch(skill -> skills.get(skill) == null);

        if (hasMissingSkill) {
            throw new BusinessRuleViolationException("All skills must receive a level");
        }

        if (signatureSkill == null || signatureSkill.isEmpty()) {
            throw new BusinessRuleViolationException("Character must have their class's signature skill");
        }

        var classSignature = characterClass.getSignature();
        var hasForeignSignature = signatureSkill.keySet().stream()
                .anyMatch(signature -> signature != classSignature);

        if (hasForeignSignature) {
            throw new BusinessRuleViolationException("Another class's signature cannot be trained");
        }

        var signatureLevel = signatureSkill.get(classSignature);

        if (signatureLevel == null || signatureLevel < CharacterSheetRules.SIGNATURE_STARTING_LEVEL) {
            throw new BusinessRuleViolationException("The signature skill starts at level 1");
        }
    }

    private static void validateSheet(
            CharacterClass characterClass,
            Map<CharacterAttribute, Integer> attributes,
            Map<CharacterSkill, Integer> skills,
            Map<SignatureSkill, Integer> signatureSkill) {

        validateSheetStructure(characterClass, attributes, skills, signatureSkill);

        var hasLevelAboveCreationCap = attributes.values().stream()
                .anyMatch(level -> level > CharacterSheetRules.ATTRIBUTE_CREATION_LEVEL_CAP);

        if (hasLevelAboveCreationCap) {
            throw new BusinessRuleViolationException("No attribute can be higher than 3 at creation");
        }

        var totalPoints = attributes.values().stream().mapToInt(Integer::intValue).sum();

        if (totalPoints != CharacterSheetRules.ATTRIBUTE_CREATION_POINTS) {
            throw new BusinessRuleViolationException("All 6 attribute points must be distributed");
        }

        var signatureLevel = signatureSkill.get(characterClass.getSignature());

        var hasSkillAboveCreationCap = Stream
                .concat(skills.values().stream(), Stream.of(signatureLevel))
                .anyMatch(level -> level > CharacterSheetRules.SKILL_CREATION_LEVEL_CAP);

        if (hasSkillAboveCreationCap) {
            throw new BusinessRuleViolationException("No skill can be higher than 2 at creation");
        }

        var spentPoints = calculateSpentSkillPoints(characterClass, skills, signatureLevel);

        if (spentPoints != CharacterSheetRules.SKILL_CREATION_POINTS) {
            throw new BusinessRuleViolationException("All 4 skill points must be distributed");
        }
    }

    private static int calculateSpentAttributePoints(Map<CharacterAttribute, Integer> attributes) {

        return attributes.values().stream()
                .mapToInt(level -> Math.min(level, CharacterSheetRules.ATTRIBUTE_CREATION_LEVEL_CAP)
                        + Math.max(0, level - CharacterSheetRules.ATTRIBUTE_CREATION_LEVEL_CAP)
                                * CharacterSheetRules.ATTRIBUTE_HIGH_LEVEL_COST)
                .sum();
    }

    private static int calculateSpentSkillPoints(
            CharacterClass characterClass,
            Map<CharacterSkill, Integer> skills,
            int signatureLevel) {

        return skills.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * resolveSkillCost(characterClass, entry.getKey()))
                .sum()
                + (signatureLevel - CharacterSheetRules.SIGNATURE_STARTING_LEVEL)
                        * CharacterSheetRules.FAVORED_SKILL_COST;
    }

    private static int resolveSkillCost(CharacterClass characterClass, CharacterSkill skill) {

        return characterClass.getFavoredSkills().contains(skill)
                ? CharacterSheetRules.FAVORED_SKILL_COST
                : CharacterSheetRules.OFF_CLASS_SKILL_COST;
    }

    public static final class Builder {

        private String name;
        private Long playerId;
        private String personality;
        private String physicalDescription;
        private CharacterClass characterClass;
        private Map<CharacterAttribute, Integer> attributes;
        private Map<CharacterSkill, Integer> skills;
        private Map<SignatureSkill, Integer> signatureSkill;

        private Builder() {
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder playerId(Long playerId) {

            this.playerId = playerId;
            return this;
        }

        public Builder personality(String personality) {

            this.personality = personality;
            return this;
        }

        public Builder physicalDescription(String physicalDescription) {

            this.physicalDescription = physicalDescription;
            return this;
        }

        public Builder characterClass(CharacterClass characterClass) {

            this.characterClass = characterClass;
            return this;
        }

        public Builder attributes(Map<CharacterAttribute, Integer> attributes) {

            this.attributes = attributes;
            return this;
        }

        public Builder skills(Map<CharacterSkill, Integer> skills) {

            this.skills = skills;
            return this;
        }

        public Builder signatureSkill(Map<SignatureSkill, Integer> signatureSkill) {

            this.signatureSkill = signatureSkill;
            return this;
        }

        public PlayerCharacter build() {

            if (isBlank(name)) {
                throw new BusinessRuleViolationException("Character name cannot be null or empty");
            }

            if (playerId == null) {
                throw new BusinessRuleViolationException("Character must have an owner");
            }

            if (isBlank(personality)) {
                throw new BusinessRuleViolationException("Character personality cannot be null or empty");
            }

            if (isBlank(physicalDescription)) {
                throw new BusinessRuleViolationException("Character physical description cannot be null or empty");
            }

            validateSheet(characterClass, attributes, skills, signatureSkill);

            return new PlayerCharacter(this);
        }
    }
}