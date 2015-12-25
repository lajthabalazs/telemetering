package hu.droidium.remote_home_manager;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public enum Relay {
	RELAY_ONE(RaspiPin.GPIO_00,"left"), RELAY_TWO(RaspiPin.GPIO_01,"right");
	private final Pin pin;
	private final String name;
	Relay(Pin pin, String name) {
		this.pin = pin;
		this.name = name;
	}
	
	public Pin getPin(){
		return pin;
	}

	public String getName() {
		return name;
	}
	
	public static Relay getRelay(String name) {
		for (Relay r : values()) {
			if (r.getName().equals(name)){
				return r;
			}
		}
		return null;
	}
}