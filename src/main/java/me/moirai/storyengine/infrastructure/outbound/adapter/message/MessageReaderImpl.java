package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessageStatus;
import me.moirai.storyengine.core.port.outbound.message.MessageReader;

@Repository
public class MessageReaderImpl implements MessageReader {

    //@formatter:off
    private static final String GET_ALL_ACTIVE_BY_ADVENTURE = """
            SELECT m.public_id,
                   au.public_id AS author_id,
                   m.author_character_name,
                   m.role,
                   m.content,
                   m.creation_date,
                   m.status
              FROM message m
              JOIN adventure a ON m.adventure_id = a.id
              LEFT JOIN moirai_user au ON au.id = m.author_id
             WHERE a.public_id = :adventurePublicId
               AND m.status = 'ACTIVE'
             ORDER BY m.creation_date ASC
            """;
    //@formatter:on

    private final JdbcClient jdbcClient;

    public MessageReaderImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<MessageSummary> getAllActiveByAdventureId(UUID adventurePublicId) {

        return jdbcClient.sql(GET_ALL_ACTIVE_BY_ADVENTURE)
                .param("adventurePublicId", adventurePublicId)
                .query(toMessageSummary())
                .list();
    }

    private RowMapper<MessageSummary> toMessageSummary() {
        return (rs, _) -> new MessageSummary(
                UUID.fromString(rs.getString("public_id")),
                MessageAuthorRole.valueOf(rs.getString("role")),
                rs.getString("content"),
                MessageStatus.valueOf(rs.getString("status")),
                rs.getObject("author_id", UUID.class),
                rs.getString("author_character_name"),
                rs.getTimestamp("creation_date").toInstant());
    }
}
