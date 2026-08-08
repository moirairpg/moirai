package me.moirai.storyengine.infrastructure.outbound.adapter.character;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.qdrant.client.QdrantGrpcClient;
import me.moirai.storyengine.AbstractQdrantIntegrationTest;

@TestInstance(Lifecycle.PER_CLASS)
public class PlayerCharacterVectorSearchAdapterIntegrationTest extends AbstractQdrantIntegrationTest {

    private static final int VECTOR_SIZE = 4;
    private static final float[] VECTOR_ALPHA = { 1f, 0f, 0f, 0f };
    private static final float[] VECTOR_BETA = { 0f, 1f, 0f, 0f };
    private static final float[] VECTOR_GAMMA = { 0f, 0f, 1f, 0f };

    private QdrantGrpcClient qdrantClient;
    private String collectionName;
    private PlayerCharacterVectorSearchAdapter adapter;

    @BeforeAll
    void setUp() {

        qdrantClient = newQdrantClient();
        collectionName = uniqueCollectionName("player-character");
        createCosineCollection(qdrantClient, collectionName, VECTOR_SIZE);
        adapter = new PlayerCharacterVectorSearchAdapter(qdrantClient, collectionName);
    }

    @AfterAll
    void tearDown() throws Exception {

        deleteCollectionIfExists(qdrantClient, collectionName);
        qdrantClient.close();
    }

    @Test
    public void upsertAndSearch_whenCharacterUpserted_thenSearchReturnsIt() {

        // given
        var characterId = UUID.randomUUID();
        adapter.upsert(characterId, VECTOR_ALPHA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(List.of(characterId), VECTOR_ALPHA, 5));

        // then
        assertThat(result).containsExactly(characterId);
    }

    @Test
    public void search_whenNonCandidateCharactersExist_thenOnlyCandidatesReturned() {

        // given
        var candidateOne = UUID.randomUUID();
        var candidateTwo = UUID.randomUUID();
        var nonCandidate = UUID.randomUUID();

        adapter.upsert(candidateOne, VECTOR_ALPHA);
        adapter.upsert(candidateTwo, VECTOR_BETA);
        adapter.upsert(nonCandidate, VECTOR_ALPHA);

        // when
        var result = awaitSize(
                () -> adapter.search(List.of(candidateOne, candidateTwo), VECTOR_ALPHA, 10),
                2);

        // then
        assertThat(result).containsExactlyInAnyOrder(candidateOne, candidateTwo);
        assertThat(result).doesNotContain(nonCandidate);
    }

    @Test
    public void search_whenMultipleCandidates_thenClosestVectorRanksFirst() {

        // given
        var alphaCharacter = UUID.randomUUID();
        var betaCharacter = UUID.randomUUID();

        adapter.upsert(alphaCharacter, VECTOR_ALPHA);
        adapter.upsert(betaCharacter, VECTOR_BETA);

        // when
        var result = awaitSize(
                () -> adapter.search(List.of(alphaCharacter, betaCharacter), VECTOR_ALPHA, 5),
                2);

        // then
        assertThat(result).containsExactly(alphaCharacter, betaCharacter);
    }

    @Test
    public void search_whenTopKIsOne_thenReturnsAtMostOneResult() {

        // given
        var first = UUID.randomUUID();
        var second = UUID.randomUUID();
        var third = UUID.randomUUID();

        adapter.upsert(first, VECTOR_ALPHA);
        adapter.upsert(second, VECTOR_BETA);
        adapter.upsert(third, VECTOR_GAMMA);

        // when
        var result = awaitSize(
                () -> adapter.search(List.of(first, second, third), VECTOR_ALPHA, 1),
                1);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    public void upsert_whenSameCharacterIdUpsertedTwice_thenReplacesInsteadOfDuplicating() {

        // given
        var characterId = UUID.randomUUID();

        adapter.upsert(characterId, VECTOR_ALPHA);
        adapter.upsert(characterId, VECTOR_BETA);

        // when
        var result = awaitNonEmpty(() -> adapter.search(List.of(characterId), VECTOR_BETA, 10));

        // then
        assertThat(result).containsExactly(characterId);
    }

    @Test
    public void delete_whenInvoked_thenOnlyTargetCharacterIsRemoved() {

        // given
        var keptCharacter = UUID.randomUUID();
        var removedCharacter = UUID.randomUUID();

        adapter.upsert(keptCharacter, VECTOR_ALPHA);
        adapter.upsert(removedCharacter, VECTOR_BETA);
        awaitSize(
                () -> adapter.search(List.of(keptCharacter, removedCharacter), VECTOR_ALPHA, 10),
                2);

        // when
        adapter.delete(removedCharacter);

        // then
        var remaining = awaitSize(
                () -> adapter.search(List.of(keptCharacter, removedCharacter), VECTOR_ALPHA, 10),
                1);
        assertThat(remaining).containsExactly(keptCharacter);
    }

    @Test
    public void search_whenCandidatesNotInCollection_thenReturnsEmptyList() {

        // given
        var orphanCandidate = UUID.randomUUID();

        // when
        var result = adapter.search(List.of(orphanCandidate), VECTOR_ALPHA, 5);

        // then
        assertThat(result).isEmpty();
    }
}
