package es.thalesalv.chatrpg.common.cqrs.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldByIdHandler;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldByIdResult;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

@ExtendWith(MockitoExtension.class)
public class QueryRunnerImplTest {

    @Test
    public void errorWhenHandlerNotFound() {

        // Given
        QueryRunnerImpl runner = new QueryRunnerImpl();
        GetWorldById query = GetWorldById.with("WLRDID");

        // Then
        assertThrows(IllegalArgumentException.class, () -> runner.run(query));
    }

    @Test
    public void errorWhenHandlerAlreadyRegistered() {

        // Given
        QueryRunnerImpl runner = new QueryRunnerImpl();
        GetWorldByIdHandler handler = mock(GetWorldByIdHandler.class);

        runner.registerHandler(handler);

        // Then
        assertThrows(IllegalArgumentException.class,
                () -> runner.registerHandler(mock(GetWorldByIdHandler.class)));
    }

    @Test
    public void errorWhenHandlerIsNull() {

        // Given
        QueryRunnerImpl runner = new QueryRunnerImpl();
        GetWorldByIdHandler handler = null;

        // Then
        assertThrows(IllegalArgumentException.class,
                () -> runner.registerHandler(handler));
    }

    @Test
    public void registerAndRun() {

        // Given
        String id = "WLRDID";
        QueryRunnerImpl runner = new QueryRunnerImpl();
        GetWorldByIdHandler handler = mock(GetWorldByIdHandler.class);
        GetWorldById query = GetWorldById.with(id);

        World world = WorldFixture.privateWorld().build();

        List<GetWorldLorebookEntry> lorebookEntries = world.getLorebook()
                .stream()
                .map(entry -> GetWorldLorebookEntry.builder()
                        .name(entry.getName())
                        .description(entry.getDescription())
                        .regex(entry.getRegex())
                        .playerDiscordId(entry.getPlayerDiscordId())
                        .build())
                .toList();

        GetWorldByIdResult expectedResult = GetWorldByIdResult.builder()
                .id(id)
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .creatorDiscordId(world.getOwnerDiscordId())
                .writerUsers(world.getWriterUsers())
                .readerUsers(world.getReaderUsers())
                .ownerDiscordId("CRTID")
                .creatorDiscordId("CRTID")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .lorebook(lorebookEntries)
                .build();

        when(handler.execute(any(GetWorldById.class))).thenReturn(expectedResult);

        runner.registerHandler(handler);

        // When
        GetWorldByIdResult result = runner.run(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
