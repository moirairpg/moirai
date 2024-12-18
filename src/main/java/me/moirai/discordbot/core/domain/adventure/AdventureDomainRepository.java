package me.moirai.discordbot.core.domain.adventure;

import java.util.Optional;

public interface AdventureDomainRepository {

    Optional<Adventure> findById(String id);

    Adventure save(Adventure adventure);

    void deleteById(String id);

    void updateRememberByChannelId(String remember, String channelId);

    void updateAuthorsNoteByChannelId(String authorsNote, String channelId);

    void updateNudgeByChannelId(String nudge, String channelId);

    void updateBumpByChannelId(String bumpContent, int bumpFrequency, String channelId);
}
