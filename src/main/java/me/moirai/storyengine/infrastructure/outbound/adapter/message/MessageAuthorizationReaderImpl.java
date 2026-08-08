package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import me.moirai.storyengine.core.port.inbound.message.MessageAuthorship;
import me.moirai.storyengine.core.port.outbound.message.MessageAuthorizationReader;

@Repository
public class MessageAuthorizationReaderImpl implements MessageAuthorizationReader {

    //@formatter:off
    private static final String GET_LAST_PLAYER_MESSAGE = """
            SELECT m.public_id,
                   au.public_id AS author_id
              FROM message m
                   JOIN adventure a ON m.adventure_id = a.id
                   LEFT JOIN moirai_user au ON au.id = m.author_id
             WHERE a.public_id = :adventurePublicId
               AND m.role      = 'USER'
               AND m.status    = 'ACTIVE'
             ORDER BY m.id DESC
             LIMIT 1
            """;

    private static final String GET_MESSAGE_AUTHOR = """
            SELECT m.public_id,
                   au.public_id AS author_id
              FROM message m
                   LEFT JOIN moirai_user au ON au.id = m.author_id
             WHERE m.public_id = :messagePublicId
               AND m.status    = 'ACTIVE'
            """;
    //@formatter:on

    private final JdbcClient jdbcClient;

    public MessageAuthorizationReaderImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<MessageAuthorship> getLastPlayerMessage(UUID adventurePublicId) {

        return jdbcClient.sql(GET_LAST_PLAYER_MESSAGE)
                .param("adventurePublicId", adventurePublicId)
                .query(toMessageAuthorship())
                .optional();
    }

    @Override
    public Optional<MessageAuthorship> getMessageAuthor(UUID messagePublicId) {

        return jdbcClient.sql(GET_MESSAGE_AUTHOR)
                .param("messagePublicId", messagePublicId)
                .query(toMessageAuthorship())
                .optional();
    }

    private RowMapper<MessageAuthorship> toMessageAuthorship() {

        return (rs, _) -> new MessageAuthorship(
                rs.getObject("public_id", UUID.class),
                rs.getObject("author_id", UUID.class));
    }
}
