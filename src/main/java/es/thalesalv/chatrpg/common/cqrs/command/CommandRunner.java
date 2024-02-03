package es.thalesalv.chatrpg.common.cqrs.command;

public interface CommandRunner {

    public <T> T run(Command<T> command);

    <A extends Command<T>, T> void registerHandler(CommandHandler<A, T> handler);
}
