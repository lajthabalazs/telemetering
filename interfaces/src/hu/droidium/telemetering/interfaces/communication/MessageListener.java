package hu.droidium.telemetering.interfaces.communication;

public interface MessageListener {
	public void messageReceived(Channel channel, String user, String message);
}