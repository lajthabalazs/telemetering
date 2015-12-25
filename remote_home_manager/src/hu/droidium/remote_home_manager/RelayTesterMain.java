package hu.droidium.remote_home_manager;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RelayTesterMain {

	public static void main(String[] args) {
		System.out.println("<--Pi4J--> GPIO Control Example ... started.");

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		// provision gpio pin #01 as an output pin and turn on
		final GpioPinDigitalOutput pin0 = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_00, "left", PinState.HIGH);
		final GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_01, "right", PinState.HIGH);

		// set shutdown state for this pin
		pin0.setShutdownOptions(true, PinState.HIGH);
		pin1.setShutdownOptions(true, PinState.HIGH);

		for (int i = 0; i < 1000; i++) {
			pin0.high();
			pin1.high();
			System.out.println("--> GPIO state should be: ON");
			sleep(10000);
			// turn off gpio pin #01
			pin0.low();
			pin1.low();
			System.out.println("--> GPIO state should be: OFF");
			sleep(10000);
		}

		gpio.shutdown();
	}

	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
