package es.thalesalv.chatrpg.common.usecases;

public interface UseCaseRunner {

    public <T> T run(UseCase<T> command);

    <A extends UseCase<T>, T> void registerHandler(UseCaseHandler<A, T> handler);
}
