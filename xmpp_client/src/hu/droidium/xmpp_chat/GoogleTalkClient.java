package hu.droidium.xmpp_chat;

import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class GoogleTalkClient implements Channel, ChatManagerListener, ChatMessageListener, ConnectionListener {
		
	private Set<MessageListener> listeners = new HashSet<MessageListener>();
	private HashMap<String, Chat> chats = new HashMap<String, Chat>();
	private XMPPTCPConnection connection;
	private ChatManager chatManager;
	private String userName;
	private String password;
	private boolean debug;

	public GoogleTalkClient(String userName, String password, boolean debug) {
		this.userName = userName;
		this.password = password;
		this.debug = debug;
		connect();
	}

	@Override
	public void reconnect() {
		disconnect();
		connect();
	}
	
	private synchronized void disconnect() {
		chats.clear();
		if (connection == null) {
			return;
		}
		if (chatManager != null) {
			chatManager.removeChatListener(this);
		}
		connection.disconnect();
		connection = null;
	}
	
	private synchronized boolean connect() {
		if (connection != null) {
			return false;
		}
		SmackConfiguration.DEBUG = debug;
		XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration.builder()
				  .setUsernameAndPassword(userName, password)
				  .setServiceName("google.com")
				  .setHost("talk.google.com")
				  .setPort(5222)
				  .setDebuggerEnabled(debug)
				  .build();
		connection = new XMPPTCPConnection(connConfig);
		connection.addConnectionListener(this);
		System.out.println(SASLAuthentication.getRegisterdSASLMechanisms().keySet());
		try {
			connection.connect();
			connection.login();
			System.out.println("Logged in as " + connection.getUser());
			Presence presence = new Presence(Presence.Type.available);
			connection.sendStanza(presence);
			chatManager = ChatManager.getInstanceFor(connection);		
			chatManager.addChatListener(this);
			return true;
		} catch (SmackException | IOException | XMPPException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean sendMessage(String user, String message) {
		System.out.println("Sending message to " + user + " message " + message);
		Chat chat = chats.get(user);
		if (chat == null) {
			if (chatManager != null) {
				chat = chatManager.createChat(user, this);
				chats.put(user, chat);
			} else {
				System.out.println("Chat manager is null. Connection " + connection);
				if (connection != null) {
					System.out.println("Connection state " + connection.isConnected());
				}
			}
		}
		try {
			chat.sendMessage(message);
			return true;
		} catch (NotConnectedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void registerMessageListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void unregisterMessageListener(MessageListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void chatCreated(Chat chat, boolean local) {
		if (local) {
			System.out.println("Local chat created " + chat.getParticipant());
		} else {
			System.out.println("Remote chat created " + chat.getParticipant());
			chat.addMessageListener(this);
		}
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		String user = message.getFrom().split("/")[0];
		String text = message.getBody();
		chats.put(user, chat);
		if (text != null) {
			System.out.println("Message received " + user + " : " + text);
			for (MessageListener listener : listeners) {
				listener.messageReceived(this, user, text);
			}
		}
	}

	@Override
	public void authenticated(XMPPConnection arg0, boolean arg1) {
		System.out.println("XMPP connection authenticated " + arg1);
	}

	@Override
	public void connected(XMPPConnection arg0) {
		System.out.println("XMPP connection connected.");
	}

	@Override
	public void connectionClosed() {
		System.out.println("XMPP connection closed.");
	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		System.out.println("XMPP connection closed with error " + arg0.getMessage());
	}

	@Override
	public void reconnectingIn(int arg0) {
		System.out.println("XMPP reconnecting in " + arg0);
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		System.out.println("XMPP reconnection failed.");
	}

	@Override
	public void reconnectionSuccessful() {
		System.out.println("XMPP reconnection successful.");
	}

	public static void main(String args[]) throws SmackException, IOException, XMPPException {
		final GoogleTalkClient talkClient = new GoogleTalkClient(args[0], args[1], true);
		MessageListener listener = new MessageListener() {
			
			@Override
			public void messageReceived(Channel channel, String user, String message) {
				System.out.println(user + " > " + message);
			}
		};
		talkClient.registerMessageListener(listener);
		new PongClient(talkClient);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}