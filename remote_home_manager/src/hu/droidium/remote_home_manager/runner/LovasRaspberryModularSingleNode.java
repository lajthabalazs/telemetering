package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.remote_home_manager.RelayController;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;
import hu.droidium.telemetering.interfaces.UserStoreInterface;
import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

public class LovasRaspberryModularSingleNode implements MessageListener {
	
	private LayoutStoreInterface layoutStore;
	private MeasurementStoreInterface measurementStore;
	private ProgramStoreInterface programStore;
	
	private final boolean demoMode;
	private RelayController relayController;
	private UserStoreInterface userStore;
	private LanguageInterface languageInterface;
	private Channel channel;
	
	public LovasRaspberryModularSingleNode(boolean demoMode, LayoutStoreInterface layoutStore, MeasurementStoreInterface measurementStore, ProgramStoreInterface programStoreInterface, UserStoreInterface userStore, RelayController relayController) {
		this.demoMode = demoMode;
		this.layoutStore = layoutStore;
		this.measurementStore = measurementStore;
		this.programStore = programStoreInterface;
		this.userStore = userStore;
		this.relayController = relayController;
	}
	
	protected void run() {
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

	public MeasurementStoreInterface getSensorDataStore() {
		return measurementStore;
	}

	public LayoutStoreInterface getLayoutStore() {
		return layoutStore;
	}

	public ProgramStoreInterface getProgramStore() {
		return programStore;
	}

	public RelayController getRelayController() {
		return relayController;
	}	
	
	public UserStoreInterface getUserStore() {
		return userStore;
	}

	@Override
	public void messageReceived(String user, String message) {
		if (userStore.hasUser(user)) {
			if (userStore.isSuperUser(user)){
				String commandResponse = processMessage(message);
				if (commandResponse == null) {
					String response = "No language interface";
					if (languageInterface != null) {
						response = languageInterface.getResponse(message, System.currentTimeMillis());
					}
					System.out.println(response);
					if (channel != null) {
						channel.sendMessage(user, response);
					} else {
						System.out.println("No channel");
					}
				} else {
					channel.sendMessage(user, commandResponse);
				}
			}
		} else {
			System.out.println("Invalid user name");
			if (channel != null) {
				channel.sendMessage(user, "User " + user + " not authorized");
			}
		}
	}

	/**
	 * Processes command message, returns true, if message was a command message, false otherwise
	 * @param message
	 * @return
	 */
	private String processMessage(String message) {
		// TODO Process command message
		message = message.toLowerCase();
		if (message.endsWith(".") || message.endsWith("!")) {
			message = message.substring(0, message.length() - 1);
		}
		Boolean add = null;
		String[] parts = message.split(" ");
		if (message.startsWith("add user ")) {
			add = true;
		} else if (message.startsWith("remove user ") || message.startsWith("del user ") || message.startsWith("delete user ")) {
			add = false;
		}
		if (add != null && parts.length == 3) {
			if (add) {
				boolean result = userStore.addUser(parts[3], false);
				if (result) {
					return "User " + parts[3] + " added.";
				} else {
					return "An unexpected error occured, couldn't add user.";
				}
			} else {
				return userStore.removeUser(parts[3]);
			}
		} else {
			if (add == null) {
				return null;
			} else {
				return "Invalid command length, requires a single parameter";
			}
		}
	}

	public void registerListener(LanguageInterface languageInterface) {
		this.languageInterface = languageInterface;
	}

	public void setLanguageInterface(Channel commClient) {
		this.channel  = commClient;
	}	

}