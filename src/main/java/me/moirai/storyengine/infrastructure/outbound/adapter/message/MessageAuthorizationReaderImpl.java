package me.moirai.storyengine.infrastructure.outbound.adapter.message;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import me.moirai.storyengine.core.port.inbound.message.MessageAuthorship;
import me.moirai.storyengine.core.port.outbound.message.MessageAuthorizationReader;

@Repository
public class MessageAuthorizationReaderImpl implements MessageAuthorizationReader {

    //@formatter:off
    private static final String GET_LAST_PLAYER_MESSAGE = """
            SELECT m.public_id,
                   m.created_by
              FROM message m
              JOIN adventure a ON m.adventure_id = a.id
             WHERE a.public_id = :adventurePublicId
               AND m.role = 'USER'
               AND m.status = 'ACTIVE'
             ORDER BY m.id DESC
             LIMIT 1
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
                .query((rs, _) -> new MessageAuthorship(
                        rs.getObject("public_id", UUID.class),
                        rs.getString("created_by")))
                .optional();
    }
}
