package me.moirai.storyengine.infrastructure.config;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.VectorsFactory.vectors;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.DefaultApplicationArguments;

import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.UpsertPoints;
import io.qdrant.client.grpc.PointsGrpc;
import me.moirai.storyengine.AbstractQdrantIntegrationTest;

@TestInstance(Lifecycle.PER_CLASS)
public class QdrantConfigIntegrationTest extends AbstractQdrantIntegrationTest {

    private static final int LOREBOOK_VECTOR_SIZE = 4;
    private static final int CHRONICLE_VECTOR_SIZE = 8;
    private static final int PLAYER_CHARACTER_VECTOR_SIZE = 16;

    private QdrantGrpcClient qdrantClient;
    private String lorebookCollection;
    private String chronicleCollection;
    private String playerCharacterCollection;
    private QdrantConfig config;

    @BeforeAll
    void setUp() {

        qdrantClient = newQdrantClient();
        lorebookCollection = uniqueCollectionName("lorebook-init");
        chronicleCollection = uniqueCollectionName("chronicle-init");
        playerCharacterCollection = uniqueCollectionName("player-character-init");
        config = new QdrantConfig(
                qdrantHost(),
                qdrantPort(),
                lorebookCollection,
                LOREBOOK_VECTOR_SIZE,
                chronicleCollection,
                CHRONICLE_VECTOR_SIZE,
                playerCharacterCollection,
                PLAYER_CHARACTER_VECTOR_SIZE);
    }

    @AfterAll
    void tearDown() throws Exception {

        deleteCollectionIfExists(qdrantClient, lorebookCollection);
        deleteCollectionIfExists(qdrantClient, chronicleCollection);
        deleteCollectionIfExists(qdrantClient, playerCharacterCollection);
        qdrantClient.close();
    }

    @Test
    public void initLorebookCollection_whenCollectionMissing_thenCreatesWithConfiguredSizeAndCosine() throws Exception {

        // given
        deleteCollectionIfExists(qdrantClient, lorebookCollection);

        // when
        config.initLorebookCollection(qdrantClient).run(new DefaultApplicationArguments());

        // then
        var info = getCollectionInfo(qdrantClient, lorebookCollection);
        var params = info.getConfig().getParams().getVectorsConfig().getParams();
        assertThat(params.getSize()).isEqualTo(LOREBOOK_VECTOR_SIZE);
        assertThat(params.getDistance()).isEqualTo(Distance.Cosine);
    }

    @Test
    public void initChronicleCollection_whenCollectionMissing_thenCreatesWithConfiguredSizeAndCosine() throws Exception {

        // given
        deleteCollectionIfExists(qdrantClient, chronicleCollection);

        // when
        config.initChronicleCollection(qdrantClient).run(new DefaultApplicationArguments());

        // then
        var info = getCollectionInfo(qdrantClient, chronicleCollection);
        var params = info.getConfig().getParams().getVectorsConfig().getParams();
        assertThat(params.getSize()).isEqualTo(CHRONICLE_VECTOR_SIZE);
        assertThat(params.getDistance()).isEqualTo(Distance.Cosine);
    }

    @Test
    public void initPlayerCharacterCollection_whenCollectionMissing_thenCreatesWithConfiguredSizeAndCosine() throws Exception {

        // given
        deleteCollectionIfExists(qdrantClient, playerCharacterCollection);

        // when
        config.initPlayerCharacterCollection(qdrantClient).run(new DefaultApplicationArguments());

        // then
        var info = getCollectionInfo(qdrantClient, playerCharacterCollection);
        var params = info.getConfig().getParams().getVectorsConfig().getParams();
        assertThat(params.getSize()).isEqualTo(PLAYER_CHARACTER_VECTOR_SIZE);
        assertThat(params.getDistance()).isEqualTo(Distance.Cosine);
    }

    @Test
    public void initLorebookCollection_whenCollectionAlreadyExists_thenDoesNotRecreate() throws Exception {

        // given
        deleteCollectionIfExists(qdrantClient, lorebookCollection);
        config.initLorebookCollection(qdrantClient).run(new DefaultApplicationArguments());

        var sentinelId = UUID.randomUUID();
        upsertSentinel(lorebookCollection, sentinelId, LOREBOOK_VECTOR_SIZE);

        // when
        config.initLorebookCollection(qdrantClient).run(new DefaultApplicationArguments());

        // then
        assertThat(collectionExists(qdrantClient, lorebookCollection)).isTrue();
        var info = getCollectionInfo(qdrantClient, lorebookCollection);
        assertThat(info.getPointsCount()).isEqualTo(1L);
    }

    @Test
    public void initLorebookCollection_whenInvoked_thenDoesNotCreateChronicleOrPlayerCharacterCollections() throws Exception {

        // given
        deleteCollectionIfExists(qdrantClient, lorebookCollection);
        deleteCollectionIfExists(qdrantClient, chronicleCollection);
        deleteCollectionIfExists(qdrantClient, playerCharacterCollection);

        // when
        config.initLorebookCollection(qdrantClient).run(new DefaultApplicationArguments());

        // then
        assertThat(collectionExists(qdrantClient, lorebookCollection)).isTrue();
        assertThat(collectionExists(qdrantClient, chronicleCollection)).isFalse();
        assertThat(collectionExists(qdrantClient, playerCharacterCollection)).isFalse();
    }

    private void upsertSentinel(String collectionName, UUID pointId, int vectorSize) {

        var points = PointsGrpc.newBlockingStub(qdrantClient.channel());
        var vector = new ArrayList<Float>(vectorSize);
        for (var i = 0; i < vectorSize; i++) {
            vector.add(i == 0 ? 1f : 0f);
        }

        var point = PointStruct.newBuilder()
                .setId(id(pointId))
                .setVectors(vectors(vector))
                .build();

        points.upsert(UpsertPoints.newBuilder()
                .setCollectionName(collectionName)
                .addAllPoints(List.of(point))
                .setWait(true)
                .build());
    }
}
