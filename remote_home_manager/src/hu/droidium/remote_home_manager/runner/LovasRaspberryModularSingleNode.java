package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.RaspberryTemperatureSensor;
import hu.droidium.remote_home_manager.RelayController;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;
import hu.droidium.telemetering.interfaces.UserStoreInterface;
import hu.droidium.telemetering.interfaces.communication.MessageListener;

public class LovasRaspberryModularSingleNode implements MessageListener {
	
	private LayoutStoreInterface layoutStore;
	private MeasurementStoreInterface measurementStore;
	private ProgramStoreInterface programStore;
	
	private final boolean demoMode;
	private RelayController relayController;
	private UserStoreInterface userStore;
	private LanguageInterface languageInterface;
	
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
		if (userStore.isSuperUser(user)){
			boolean commandMessage = processMessage(message);
			if (!commandMessage) {
				languageInterface.getResponse(message, System.currentTimeMillis());
			}
		}
		if (userStore.hasUser(user)) {
			languageInterface.getResponse(message, System.currentTimeMillis());
		}
	}

	private boolean processMessage(String message) {
		// TODO Auto-generated method stub
		return false;
	}

	public void registerListener(LanguageInterface languageInterface) {
		this.languageInterface = languageInterface;
	}	

}