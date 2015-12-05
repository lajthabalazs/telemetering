package hu.droidium.telemetering.interfaces.communication;

public interface MessageListener {
	public void messageReceived(String user, String message);
}