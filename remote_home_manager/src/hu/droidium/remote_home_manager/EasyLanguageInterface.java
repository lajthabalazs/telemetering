package hu.droidium.remote_home_manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import hu.droidium.remote_home_manager.runner.LovasRaspberryModularSingleNode;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.LayoutStoreInterface;
import hu.droidium.telemetering.interfaces.Measurement;
import hu.droidium.telemetering.interfaces.MeasurementStoreInterface;
import hu.droidium.telemetering.interfaces.ProgramStoreInterface;
import hu.droidium.telemetering.interfaces.SensorType;

public class EasyLanguageInterface implements LanguageInterface {

	private MeasurementStoreInterface sensorDataStore;
	@SuppressWarnings("unused")
	private LayoutStoreInterface layoutStore;
	@SuppressWarnings("unused")
	private ProgramStoreInterface programStore;
	private RelayController relayController;
	
	SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm Z");


	public EasyLanguageInterface( LayoutStoreInterface layoutStore, MeasurementStoreInterface sensorDataStore, ProgramStoreInterface programStore, RelayController relayController) {
		this.sensorDataStore = sensorDataStore;
		this.layoutStore = layoutStore;
		this.programStore = programStore;
		this.relayController = relayController;
	}
	
	public EasyLanguageInterface(LovasRaspberryModularSingleNode node) {
		this.sensorDataStore = node.getSensorDataStore();
		this.layoutStore = node.getLayoutStore();
		this.programStore = node.getProgramStore();
		this.relayController = node.getRelayController();
	}

	
	@Override
	public String getResponse(String message, long time) {
		message = message.toLowerCase().trim();
		if (message.startsWith("hom")){
			Measurement data = sensorDataStore.getLastMeasurement("nappali", SensorType.TEMPERATURE);
			if (data != null) {
				return format.format(new Date(data.getTime())) + " > " + data.getValueString();
			} else {
				return "No data available";
			}
		} else {
			String[] parts = message.split(" ");
			if (parts.length == 2){
				Relay relay;
				if (parts[0].equals("bal")) {
					relay = Relay.RELAY_ONE;
				} else if (parts[0].equals("jobb")){
					relay = Relay.RELAY_TWO;
				} else {
					relay = null;
				}
				if (relay != null) {
					if (parts[1].equals("ki")){
						relayController.setState(relay, RelayState.OFF);
						return relay.getName() + " OFF";
					} else if (parts[1].equals("be")){
						relayController.setState(relay, RelayState.ON);
						return relay.getName() + " ON";
					}
				}
			}
		}
		return "Elfogadott parancsok:\nhom: Visszaadja a legutoljára mért hőmérsékletet\n[bal|jobb] [ki|be]: Az adott relét kapcsolja, pld: bal ki";
	}
}