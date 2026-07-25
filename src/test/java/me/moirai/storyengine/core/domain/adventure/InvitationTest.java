package me.moirai.storyengine.core.domain.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import me.moirai.storyengine.common.enums.InvitationStatus;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public class InvitationTest {

    @Test
    public void shouldCreateInvitationAsPendingWithAPublicId() {

        // when
        var invitation = Invitation.builder().adventureId(1L).userId(1L).build();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitation.isPending()).isTrue();
        assertThat(invitation.getPublicId()).isNotNull();
        assertThat(invitation.getUserId()).isEqualTo(1L);
    }

    @Test
    public void shouldThrowWhenBuildingWithoutRecipient() {

        // then
        assertThatThrownBy(() -> Invitation.builder().adventureId(1L).build())
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void shouldThrowWhenBuildingWithoutAdventure() {

        // then
        assertThatThrownBy(() -> Invitation.builder().userId(1L).build())
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void shouldTransitionToAcceptedFromPending() {

        // given
        var invitation = Invitation.builder().adventureId(1L).userId(1L).build();

        // when
        invitation.accept();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }

    @Test
    public void shouldTransitionToDeclinedFromPending() {

        // given
        var invitation = Invitation.builder().adventureId(1L).userId(1L).build();

        // when
        invitation.decline();

        // then
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.DECLINED);
    }

    @Test
    public void shouldThrowWhenAcceptingAnAlreadyAnsweredInvitation() {

        // given
        var invitation = Invitation.builder().adventureId(1L).userId(1L).build();
        invitation.accept();

        // then
        assertThatThrownBy(invitation::accept).isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    public void shouldThrowWhenDecliningAnAlreadyAnsweredInvitation() {

        // given
        var invitation = Invitation.builder().adventureId(1L).userId(1L).build();
        invitation.decline();

        // then
        assertThatThrownBy(invitation::decline).isInstanceOf(BusinessRuleViolationException.class);
    }
}
