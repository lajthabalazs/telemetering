package hu.droidium.telemetering.interfaces.communication;


public interface Channel {
	public boolean sendMessage(String user, String message);
	public void registerMessageListener(MessageListener listener);
	public void unregisterMessageListener(MessageListener listener);
	public void reconnect();

}
