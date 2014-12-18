package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.remote_home_manager.SQLJetDatastore;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.SensorType;
import main.TelemeteringIRCClient;
import main.TelemeteringIRCListener;

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
						temp = (long) (Math.random() * 10 + 15);
					} else {
						temp = RaspberryTemperatureSensor.measure() / 1000;
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
		if (args.length == 6) {
			databaseFile = args[0];
			userName = args[1];
			chatServer = args[2];
			port = Integer.parseInt(args[3]);
			chatRoom = args[4];
			demoMode = Boolean.parseBoolean(args[5]);
		} else {
			databaseFile = "temp.sqlite";
			userName = "lovas_telemetering_client";
			chatServer = "irc.chatjunkies.org";
			port = 6667;
			chatRoom = "#xchat";
			demoMode = true;
		}
		LovasRaspberrySingleNode node = new LovasRaspberrySingleNode(databaseFile, demoMode);
		node.run();		
		LanguageInterface languageInterface = new HungarianLanguageModule(node);
		TelemeteringIRCListener listener = new TelemeteringIRCListener(languageInterface);
		new TelemeteringIRCClient(userName, chatServer, port, chatRoom, listener);
	}
}