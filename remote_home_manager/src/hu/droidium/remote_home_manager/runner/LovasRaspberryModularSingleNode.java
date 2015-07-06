package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.GUIClient;
import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;

import main.TelemeteringIRCClient;

public class LovasRaspberryModularSingleNode {
	
	private LayoutStoreInterface layoutStore;
	private MeasurementStoreInterface measurementStore;
	private ProgramStoreInterface programStore;
	
	private final boolean demoMode;
	
	public LovasRaspberryModularSingleNode(boolean demoMode) {
		this.demoMode = demoMode;
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
					measurementStore.saveMeasurement(layoutStore.getLocations().get(0), SensorType.TEMPERATURE, System.currentTimeMillis(), temp);
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

		LovasRaspberryModularSingleNode node = new LovasRaspberryModularSingleNode(demoMode);
		node.run();		
		LanguageInterface languageInterface = new HungarianLanguageModule(node.layoutStore, node.measurementStore, node.programStore);
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