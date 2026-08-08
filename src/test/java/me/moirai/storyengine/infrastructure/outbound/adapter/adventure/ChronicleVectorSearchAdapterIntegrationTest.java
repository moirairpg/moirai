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
public class ChronicleVectorSearchAdapterIntegrationTest extends AbstractQdrantIntegrationTest {

    private static final int VECTOR_SIZE = 4;
    private static final float[] VECTOR_ALPHA = { 1f, 0f, 0f, 0f };
    private static final float[] VECTOR_BETA = { 0f, 1f, 0f, 0f };
    private static final float[] VECTOR_GAMMA = { 0f, 0f, 1f, 0f };

    private QdrantGrpcClient qdrantClient;
    private String collectionName;
    private ChronicleVectorSearchAdapter adapter;

    @BeforeAll
    void setUp() {

        qdrantClient = newQdrantClient();
        collectionName = uniqueCollectionName("chronicle");
        createCosineCollection(qdrantClient, collectionName, VECTOR_SIZE);
        adapter = new ChronicleVectorSearchAdapter(qdrantClient, collectionName);
    }

    @AfterAll
    void tearDown() throws Exception {

        deleteCollectionIfExists(qdrantClient, collectionName);
        qdrantClient.close();
    }

    @Test
    public void upsertAndSearch_whenSegmentUpserted_thenSearchReturnsIt() {

        // given
        var adventureId = UUID.randomUUID();
        var segmentId = UUID.randomUUID();

        // when
        adapter.upsert(adventureId, segmentId, VECTOR_ALPHA);
        var result = awaitNonEmpty(() -> adapter.search(adventureId, VECTOR_ALPHA, 5));

        // then
        assertThat(result).containsExactly(segmentId);
    }

    @Test
    public void search_whenMultipleSegments_thenClosestVectorRanksFirst() {

        // given
        var adventureId = UUID.randomUUID();
        var alphaSegment = UUID.randomUUID();
        var betaSegment = UUID.randomUUID();

        adapter.upsert(adventureId, alphaSegment, VECTOR_ALPHA);
        adapter.upsert(adventureId, betaSegment, VECTOR_BETA);

        // when
        var result = awaitSize(() -> adapter.search(adventureId, VECTOR_ALPHA, 5), 2);

        // then
        assertThat(result).containsExactly(alphaSegment, betaSegment);
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
    public void search_whenAnotherAdventureHasSegments_thenScopeIsRespected() {

        // given
        var adventureA = UUID.randomUUID();
        var adventureB = UUID.randomUUID();
        var segmentA = UUID.randomUUID();
        var segmentB = UUID.randomUUID();

        adapter.upsert(adventureA, segmentA, VECTOR_ALPHA);
        adapter.upsert(adventureB, segmentB, VECTOR_ALPHA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(adventureA, VECTOR_ALPHA, 10));

        // then
        assertThat(result).containsExactly(segmentA);
    }

    @Test
    public void upsert_whenSameSegmentIdUpsertedTwice_thenReplacesInsteadOfDuplicating() {

        // given
        var adventureId = UUID.randomUUID();
        var segmentId = UUID.randomUUID();

        adapter.upsert(adventureId, segmentId, VECTOR_ALPHA);
        adapter.upsert(adventureId, segmentId, VECTOR_BETA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(adventureId, VECTOR_BETA, 10));

        // then
        assertThat(result).containsExactly(segmentId);
    }

    @Test
    public void deleteAllByAdventureId_whenInvoked_thenOnlyThatAdventureIsWiped() {

        // given
        var adventureA = UUID.randomUUID();
        var adventureB = UUID.randomUUID();
        var segmentA = UUID.randomUUID();
        var segmentB = UUID.randomUUID();

        adapter.upsert(adventureA, segmentA, VECTOR_ALPHA);
        adapter.upsert(adventureB, segmentB, VECTOR_BETA);
        awaitNonEmpty(() -> adapter.search(adventureA, VECTOR_ALPHA, 10));
        awaitNonEmpty(() -> adapter.search(adventureB, VECTOR_BETA, 10));

        // when
        adapter.deleteAllByAdventureId(adventureA);

        // then
        var remainingInA = awaitSize(() -> adapter.search(adventureA, VECTOR_ALPHA, 10), 0);
        var remainingInB = awaitNonEmpty(() -> adapter.search(adventureB, VECTOR_BETA, 10));
        assertThat(remainingInA).isEmpty();
        assertThat(remainingInB).containsExactly(segmentB);
    }

    @Test
    public void search_whenAdventureHasNoSegments_thenReturnsEmptyList() {

        // given
        var adventureId = UUID.randomUUID();

        // when
        var result = adapter.search(adventureId, VECTOR_ALPHA, 5);

        // then
        assertThat(result).isEmpty();
    }
}
