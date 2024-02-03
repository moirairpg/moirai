package es.thalesalv.chatrpg.common.cqrs.query;

public interface QueryRunner {

    public <T> T run(Query<T> query);

    <A extends Query<T>, T> void registerHandler(QueryHandler<A, T> handler);
}
