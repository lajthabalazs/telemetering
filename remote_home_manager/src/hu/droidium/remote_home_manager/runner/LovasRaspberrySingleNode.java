package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.GUIClient;
import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.remote_home_manager.SQLJetDatastore;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.SensorType;
import main.TelemeteringIRCClient;

public class LovasRaspberrySingleNode extends SQLJetDatastore{
	
	private static final String[] locations = {"a nappaliban"};
	private final boolean demoMode;
	
	public LovasRaspberrySingleNode(String databaseFile, boolean demoMode) {
		super(databaseFile);
		this.demoMode = demoMode;
	}
	
	@Override
	public String[] getLocations() {
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
					saveMeasurement(locations[0], SensorType.TEMPERATURE, System.currentTimeMillis(), temp);
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
		if (args.length == 6) {
			databaseFile = args[0];
			userName = args[1];
			chatServer = args[2];
			port = Integer.parseInt(args[3]);
			chatRoom = args[4];
			demoMode = Boolean.parseBoolean(args[5]);
			ui = false;
		} else {
			databaseFile = "temp.sqlite";
			userName = "lovas_telemetering_client";
			chatServer = "irc.chatjunkies.org";
			port = 6667;
			chatRoom = "#xchat";
			demoMode = true;
			ui = true;
		}
		LovasRaspberrySingleNode node = new LovasRaspberrySingleNode(databaseFile, demoMode);
		node.run();		
		LanguageInterface languageInterface = new HungarianLanguageModule(node);
		new TelemeteringIRCClient(userName, chatServer, port, chatRoom, languageInterface);
		if (ui) {
			new GUIClient(languageInterface);
		}
	}
}