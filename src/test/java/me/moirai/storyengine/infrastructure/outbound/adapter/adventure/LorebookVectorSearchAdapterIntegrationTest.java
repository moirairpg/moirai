package me.moirai.storyengine.infrastructure.outbound.adapter.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.qdrant.client.QdrantGrpcClient;
import me.moirai.storyengine.AbstractQdrantIntegrationTest;

@TestInstance(Lifecycle.PER_CLASS)
public class LorebookVectorSearchAdapterIntegrationTest extends AbstractQdrantIntegrationTest {

    private static final int VECTOR_SIZE = 4;
    private static final float[] VECTOR_ALPHA = { 1f, 0f, 0f, 0f };
    private static final float[] VECTOR_BETA = { 0f, 1f, 0f, 0f };
    private static final float[] VECTOR_GAMMA = { 0f, 0f, 1f, 0f };

    private QdrantGrpcClient qdrantClient;
    private String collectionName;
    private LorebookVectorSearchAdapter adapter;

    @BeforeAll
    void setUp() {

        qdrantClient = newQdrantClient();
        collectionName = uniqueCollectionName("lorebook");
        createCosineCollection(qdrantClient, collectionName, VECTOR_SIZE);
        adapter = new LorebookVectorSearchAdapter(qdrantClient, collectionName);
    }

    @AfterAll
    void tearDown() throws Exception {

        deleteCollectionIfExists(qdrantClient, collectionName);
        qdrantClient.close();
    }

    @Test
    public void upsertAndSearch_whenEntryUpserted_thenSearchReturnsIt() {

        // given
        var adventureId = UUID.randomUUID();
        var entryId = UUID.randomUUID();

        // when
        adapter.upsert(adventureId, entryId, VECTOR_ALPHA);
        var result = awaitNonEmpty(() -> adapter.search(adventureId, VECTOR_ALPHA, 5));

        // then
        assertThat(result).containsExactly(entryId);
    }

    @Test
    public void search_whenMultipleEntries_thenClosestVectorRanksFirst() {

        // given
        var adventureId = UUID.randomUUID();
        var alphaEntry = UUID.randomUUID();
        var betaEntry = UUID.randomUUID();

        adapter.upsert(adventureId, alphaEntry, VECTOR_ALPHA);
        adapter.upsert(adventureId, betaEntry, VECTOR_BETA);

        // when
        var result = awaitSize(() -> adapter.search(adventureId, VECTOR_ALPHA, 5), 2);

        // then
        assertThat(result).containsExactly(alphaEntry, betaEntry);
    }

    @Test
    public void search_whenTopKIsOne_thenReturnsAtMostOneResult() {

        // given
        var adventureId = UUID.randomUUID();
        adapter.upsert(adventureId, UUID.randomUUID(), VECTOR_ALPHA);
        adapter.upsert(adventureId, UUID.randomUUID(), VECTOR_BETA);
        adapter.upsert(adventureId, UUID.randomUUID(), VECTOR_GAMMA);

        // when
        var result = awaitSize(() -> adapter.search(adventureId, VECTOR_ALPHA, 1), 1);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void search_whenAnotherAdventureHasEntries_thenScopeIsRespected() {

        // given
        var adventureA = UUID.randomUUID();
        var adventureB = UUID.randomUUID();
        var entryA = UUID.randomUUID();
        var entryB = UUID.randomUUID();

        adapter.upsert(adventureA, entryA, VECTOR_ALPHA);
        adapter.upsert(adventureB, entryB, VECTOR_ALPHA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(adventureA, VECTOR_ALPHA, 10));

        // then
        assertThat(result).containsExactly(entryA);
    }

    @Test
    public void upsert_whenSameEntryIdUpsertedTwice_thenReplacesInsteadOfDuplicating() {

        // given
        var adventureId = UUID.randomUUID();
        var entryId = UUID.randomUUID();

        adapter.upsert(adventureId, entryId, VECTOR_ALPHA);
        adapter.upsert(adventureId, entryId, VECTOR_BETA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(adventureId, VECTOR_BETA, 10));

        // then
        assertThat(result).containsExactly(entryId);
    }

    @Test
    public void delete_whenInvoked_thenOnlyTargetEntryIsRemoved() {

        // given
        var adventureId = UUID.randomUUID();
        var keptEntry = UUID.randomUUID();
        var removedEntry = UUID.randomUUID();

        adapter.upsert(adventureId, keptEntry, VECTOR_ALPHA);
        adapter.upsert(adventureId, removedEntry, VECTOR_BETA);
        awaitSize(() -> adapter.search(adventureId, VECTOR_ALPHA, 10), 2);

        // when
        adapter.delete(removedEntry);

        // then
        var remaining = awaitSize(() -> adapter.search(adventureId, VECTOR_ALPHA, 10), 1);
        assertThat(remaining).containsExactly(keptEntry);
    }

    @Test
    public void deleteAllByAdventureId_whenInvoked_thenOnlyThatAdventureIsWiped() {

        // given
        var adventureA = UUID.randomUUID();
        var adventureB = UUID.randomUUID();
        var entryA = UUID.randomUUID();
        var entryB = UUID.randomUUID();

        adapter.upsert(adventureA, entryA, VECTOR_ALPHA);
        adapter.upsert(adventureB, entryB, VECTOR_BETA);
        awaitNonEmpty(() -> adapter.search(adventureA, VECTOR_ALPHA, 10));
        awaitNonEmpty(() -> adapter.search(adventureB, VECTOR_BETA, 10));

        // when
        adapter.deleteAllByAdventureId(adventureA);

        // then
        var remainingInA = awaitSize(() -> adapter.search(adventureA, VECTOR_ALPHA, 10), 0);
        var remainingInB = awaitNonEmpty(() -> adapter.search(adventureB, VECTOR_BETA, 10));
        assertThat(remainingInA).isEmpty();
        assertThat(remainingInB).containsExactly(entryB);
    }

    @Test
    public void search_whenAdventureHasNoEntries_thenReturnsEmptyList() {

        // given
        var adventureId = UUID.randomUUID();

        // when
        var result = adapter.search(adventureId, VECTOR_ALPHA, 5);

        // then
        assertThat(result).isEmpty();
    }
}
