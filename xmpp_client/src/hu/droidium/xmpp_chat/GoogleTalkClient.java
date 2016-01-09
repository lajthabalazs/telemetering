package hu.droidium.xmpp_chat;

import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class GoogleTalkClient implements Channel, ChatManagerListener, ChatMessageListener {

	static {
		SmackConfiguration.DEBUG = true;
	}
	private Set<MessageListener> listeners = new HashSet<MessageListener>();
	private HashMap<String, Chat> chats = new HashMap<String, Chat>();
	private XMPPTCPConnection connection;
	private ChatManager chatManager;
	private String userName;
	private String password;
	private boolean debug;

	public GoogleTalkClient(String userName, String password, boolean debug) throws SmackException, IOException, XMPPException {
		this.userName = userName;
		this.password = password;
		this.debug = debug;
		connect();
	}

	@Override
	public void reconnect() {
		disconnect();
		try {
			connect();
		} catch (SmackException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
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
	
	private synchronized void connect() throws SmackException, IOException, XMPPException {
		if (connection != null) {
			return;
		}
		XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration.builder()
				  .setUsernameAndPassword(userName, password)
				  .setServiceName("google.com")
				  .setHost("talk.google.com")
				  .setPort(5222)
				  .setDebuggerEnabled(debug)
				  .build();
		connection = new XMPPTCPConnection(connConfig);
		System.out.println("Connecting...");
		connection.connect();
		System.out.println("Connected.");
		System.out.println("Logging in...");
		connection.login();
		System.out.println("Logged in as " + connection.getUser());
		Presence presence = new Presence(Presence.Type.available);
		connection.sendStanza(presence);
		chatManager = ChatManager.getInstanceFor(connection);		
		chatManager.addChatListener(this);
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