package hu.droidium.remote_home_manager;

import java.util.HashMap;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class RaspberryRelayController implements RelayController{
	
	private GpioController gpio = GpioFactory.getInstance();
	private HashMap<Relay, GpioPinDigitalOutput> outputs = new HashMap<Relay, GpioPinDigitalOutput>();

	public RaspberryRelayController() {
		for (Relay relay : Relay.values()) {
			GpioPinDigitalOutput output = gpio.provisionDigitalOutputPin(relay.getPin(), relay.getName(), PinState.HIGH);
			output.setShutdownOptions(false);
			outputs.put(relay, output);
			
		}
	}
	
	@Override
	public void setState(Relay relay, RelayState state){
		switch (state) {
			case ON : {
				outputs.get(relay).low();
				break;
			}
			case OFF : {
				outputs.get(relay).high();
				break;
			}
		}
	}
}
