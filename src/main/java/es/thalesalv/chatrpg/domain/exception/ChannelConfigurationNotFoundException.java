package es.thalesalv.chatrpg.domain.exception;

public class ChannelConfigurationNotFoundException extends RuntimeException {

    public ChannelConfigurationNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public ChannelConfigurationNotFoundException(String msg) {

        super(msg);
    }
}
