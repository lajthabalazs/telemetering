package hu.droidium.xmpp_chat;

import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

public class GoogleTalkClient implements Channel, ChatManagerListener, ChatMessageListener {
	
	private Set<MessageListener> listeners = new HashSet<MessageListener>();
	private HashMap<String, Chat> chats = new HashMap<String, Chat>();
	private XMPPTCPConnection connection;
	private ChatManager chatManager;
	private VCardManager cardManager;
	
	public GoogleTalkClient(String userName, String password) throws SmackException, IOException, XMPPException {
		SmackConfiguration.DEBUG = true;
		XMPPTCPConnectionConfiguration connConfig = XMPPTCPConnectionConfiguration.builder()
				  .setUsernameAndPassword(userName, password)
				  .setServiceName("google.com")
				  .setHost("talk.google.com")
				  .setPort(5222)
				  .setDebuggerEnabled(true)
				  .build();
		connection = new XMPPTCPConnection(connConfig);
		System.out.println(SASLAuthentication.getRegisterdSASLMechanisms().keySet());
		connection.connect();
		connection.login();
		System.out.println("Logged in as " + connection.getUser());
		Presence presence = new Presence(Presence.Type.available);
		connection.sendStanza(presence);
		chatManager = ChatManager.getInstanceFor(connection);		
		chatManager.addChatListener(this);
		cardManager = VCardManager.getInstanceFor(connection);
	}
	
	@Override
	public boolean sendMessage(String user, String message) {
		Chat chat = chats.get(user);
		if (chat == null) {
			chat = chatManager.createChat("lajthabalazs@gmail.com", this);
			chat.addMessageListener(this);
			chats.put(user, chat);
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
			System.out.println("Remote chat created");
			chat.addMessageListener(this);
		}
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		String user = message.getFrom();
		String text = message.getBody();
		try {
			VCard c = cardManager.loadVCard(user);
			System.out.println(c.getEmailHome() + " " + c.getEmailWork());
			System.out.println(c);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			e.printStackTrace();
		}
		if (text != null) {
			System.err.println(user + ">" + text);
			for (MessageListener listener : listeners) {
				listener.messageReceived(user, text);
			}
		}
	}

	public static void main(String args[]) throws SmackException, IOException, XMPPException {
		new GoogleTalkClient(args[0], args[1]);
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