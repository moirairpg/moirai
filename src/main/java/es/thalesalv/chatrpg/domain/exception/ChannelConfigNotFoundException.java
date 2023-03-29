package es.thalesalv.chatrpg.domain.exception;

public class ChannelConfigNotFoundException extends RuntimeException {

    public ChannelConfigNotFoundException() {

        super();
    }

    public ChannelConfigNotFoundException(String msg) {

        super(msg);
    }
}
