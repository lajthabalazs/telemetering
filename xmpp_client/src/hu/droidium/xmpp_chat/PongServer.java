package hu.droidium.xmpp_chat;

import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

public class PongServer implements MessageListener{
	public static final String PONG_SERVER = "2eh2f7x6y7grt3j8ti9fxium7j@public.talk.google.com";
	public static final int PING_DELAY = 10000;
	public static final int CONNECTION_LOST_COUNT = 4;

	private Thread watchdog;
	private int noPacketsReceived = 0;
	boolean running = true;
	private Channel channel;
	
	
	public PongServer(Channel channel) {
		this.channel = channel;
		channel.registerMessageListener(this);
		watchdog = new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
					try {
						Thread.sleep(PING_DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (noPacketsReceived >= CONNECTION_LOST_COUNT) {
						noPacketsReceived = 0;
						System.out.println("Pong server not reachable");
						System.out.println("Reconnecting ...");
						PongServer.this.channel.reconnect();
					} else if (noPacketsReceived > 0) {
						System.out.println("Pong server haven't received messages for " + noPacketsReceived * PING_DELAY / 1000 + " seconds.");
					}
					noPacketsReceived ++;
				}
			}
		});
		watchdog.start();

	}

	@Override
	public void messageReceived(Channel channel, String user, String message) {
		if (message != null) {
			message = message.toLowerCase().trim();
		}
		System.out.println(user + " > " + message);
		if (message.equals("ping")) {
			channel.sendMessage(user, "pong");
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Pong server requires a username and a password.");
		} else {
			Channel channel = new GoogleTalkClient(args[0], args[1], false);
			new PongServer(channel);
		}
	}
}