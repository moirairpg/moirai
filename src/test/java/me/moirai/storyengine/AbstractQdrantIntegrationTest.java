package me.moirai.storyengine;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.CollectionExistsRequest;
import io.qdrant.client.grpc.Collections.CollectionInfo;
import io.qdrant.client.grpc.Collections.CreateCollection;
import io.qdrant.client.grpc.Collections.DeleteCollection;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.GetCollectionInfoRequest;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Collections.VectorsConfig;
import io.qdrant.client.grpc.CollectionsGrpc;

public abstract class AbstractQdrantIntegrationTest {

    private static final String QDRANT_IMAGE_NAME = "qdrant/qdrant:v1.17.1";
    private static final int QDRANT_GRPC_PORT = 6334;
    private static final int VISIBILITY_POLL_ATTEMPTS = 20;
    private static final Duration VISIBILITY_POLL_INTERVAL = Duration.ofMillis(50);

    @SuppressWarnings("resource")
    protected static final GenericContainer<?> qdrantContainer = new GenericContainer<>(QDRANT_IMAGE_NAME)
            .withExposedPorts(QDRANT_GRPC_PORT)
            .waitingFor(Wait.forListeningPort());

    static {
        qdrantContainer.start();
    }

    protected static String qdrantHost() {
        return qdrantContainer.getHost();
    }

    protected static int qdrantPort() {
        return qdrantContainer.getMappedPort(QDRANT_GRPC_PORT);
    }

    protected static QdrantGrpcClient newQdrantClient() {
        return QdrantGrpcClient.newBuilder(qdrantHost(), qdrantPort(), false).build();
    }

    protected static String uniqueCollectionName(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    protected static void createCosineCollection(QdrantGrpcClient client, String name, int vectorSize) {

        var collections = CollectionsGrpc.newBlockingStub(client.channel());
        collections.create(CreateCollection.newBuilder()
                .setCollectionName(name)
                .setVectorsConfig(VectorsConfig.newBuilder()
                        .setParams(VectorParams.newBuilder()
                                .setSize(vectorSize)
                                .setDistance(Distance.Cosine)
                                .build())
                        .build())
                .build());
    }

    protected static void deleteCollectionIfExists(QdrantGrpcClient client, String name) {

        var collections = CollectionsGrpc.newBlockingStub(client.channel());
        if (collectionExists(client, name)) {
            collections.delete(DeleteCollection.newBuilder()
                    .setCollectionName(name)
                    .build());
        }
    }

    protected static boolean collectionExists(QdrantGrpcClient client, String name) {

        var collections = CollectionsGrpc.newBlockingStub(client.channel());
        return collections.collectionExists(CollectionExistsRequest.newBuilder()
                .setCollectionName(name)
                .build())
                .getResult()
                .getExists();
    }

    protected static CollectionInfo getCollectionInfo(QdrantGrpcClient client, String name) {

        var collections = CollectionsGrpc.newBlockingStub(client.channel());
        return collections.get(GetCollectionInfoRequest.newBuilder()
                .setCollectionName(name)
                .build())
                .getResult();
    }

    protected static <T> List<T> awaitNonEmpty(Supplier<List<T>> supplier) {

        for (var attempt = 0; attempt < VISIBILITY_POLL_ATTEMPTS; attempt++) {
            var result = supplier.get();
            if (!result.isEmpty()) {
                return result;
            }
            sleep(VISIBILITY_POLL_INTERVAL);
        }

        return supplier.get();
    }

    protected static <T> List<T> awaitSize(Supplier<List<T>> supplier, int expectedSize) {

        for (var attempt = 0; attempt < VISIBILITY_POLL_ATTEMPTS; attempt++) {
            var result = supplier.get();
            if (result.size() == expectedSize) {
                return result;
            }
            sleep(VISIBILITY_POLL_INTERVAL);
        }

        return supplier.get();
    }

    private static void sleep(Duration duration) {

        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Qdrant", e);
        }
    }
}
