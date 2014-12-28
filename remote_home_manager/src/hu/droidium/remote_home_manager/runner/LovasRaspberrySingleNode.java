package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.GUIClient;
import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.remote_home_manager.SQLJetDatastore;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.SensorType;

import java.util.LinkedList;
import java.util.List;

import main.TelemeteringIRCClient;

public class LovasRaspberrySingleNode extends SQLJetDatastore{
	
	private static final List<String> locations = new LinkedList<String>();
	static {
		locations.add("a nappaliban");
	}
	private final boolean demoMode;
	
	public LovasRaspberrySingleNode(String databaseFile, boolean demoMode) {
		super(databaseFile);
		System.out.println("Constructor run.");
		this.demoMode = demoMode;
	}
	
	@Override
	public List<String> getLocations() {
		return locations;
	}
	
	private void run() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Run node!");
				while (true){
					System.out.println("Measuring...");
					long temp;
					if (demoMode) {
						temp = (long) (Math.random() * 10000 + 15000);
					} else {
						temp = RaspberryTemperatureSensor.measure();
					}
					saveMeasurement(locations.get(0), SensorType.TEMPERATURE, System.currentTimeMillis(), temp);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		String databaseFile;
		String userName;
		String chatServer;
		int port;
		String chatRoom;
		boolean demoMode;
		boolean ui;
		System.out.println("Received " + args.length + " parameters.");
		if (args.length == 6) {
			System.out.println("Extracting parameters...");
			databaseFile = args[0];
			userName = args[1];
			chatServer = args[2];
			port = Integer.parseInt(args[3]);
			chatRoom = args[4];
			demoMode = Boolean.parseBoolean(args[5]);
			ui = false;
		} else {
			System.out.println("Using built in parameters...");
			databaseFile = "temp.sqlite";
			userName = "lovas_telemetering_client";
			chatServer = "irc.chatjunkies.org";
			port = 6667;
			chatRoom = "#xchat";
			demoMode = true;
			ui = true;
		}
		System.out.println("Database " + databaseFile);
		System.out.println("User " + userName);
		System.out.println("Chat server " + chatServer);
		System.out.println("Port " + port);
		System.out.println("Chat room " + chatRoom);
		System.out.println("Demo mode " + demoMode);

		LovasRaspberrySingleNode node = new LovasRaspberrySingleNode(databaseFile, demoMode);
		node.run();		
		LanguageInterface languageInterface = new HungarianLanguageModule(node);
		while (true) {
			try {
				new TelemeteringIRCClient(userName, chatServer, port, chatRoom, languageInterface);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error " + e);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (ui) {
			new GUIClient(languageInterface);
		}
	}
}