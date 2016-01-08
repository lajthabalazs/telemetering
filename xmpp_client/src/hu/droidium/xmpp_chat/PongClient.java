package hu.droidium.xmpp_chat;

import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

public class PongClient  implements MessageListener {
	
	private Thread watchdog;
	private int packetsSentSinceLastPong = 0;
	private Channel channel;
	// TODO report trouble to sysadmin
	private boolean running = true;

	public PongClient(Channel channel) {
		this.channel = channel;
		channel.registerMessageListener(this);
		watchdog = new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
					try {
						Thread.sleep(PongServer.PING_DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (packetsSentSinceLastPong >= PongServer.CONNECTION_LOST_COUNT) {
						packetsSentSinceLastPong = 0;
						System.out.println("Pong server not reachable");
						System.out.println("Reconnecting ...");
						PongClient.this.channel.reconnect();
					} else if (packetsSentSinceLastPong > 0) {
						System.out.println("Pong server haven't responded to " + packetsSentSinceLastPong + " messages.");
					}
					boolean result = channel.sendMessage(PongServer.PONG_SERVER, "ping");
					if (!result) {
						System.err.println("Client was unable to send message.");
					}
					packetsSentSinceLastPong ++;
				}
			}
		});
		watchdog.start();
	}

	public void stop() {
		running = false;
		try {
			watchdog.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void messageReceived(Channel channel, String user, String message) {
		packetsSentSinceLastPong = 0;
	}
}