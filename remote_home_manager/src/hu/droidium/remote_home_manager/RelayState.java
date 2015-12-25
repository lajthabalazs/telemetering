package hu.droidium.remote_home_manager;

import com.pi4j.io.gpio.PinState;

public enum RelayState {
	ON(PinState.LOW, "bekapcsolva"), OFF(PinState.HIGH, "kikapcsolva");
	private final PinState state;
	private final String resultStateString;

	RelayState(PinState state, String resultStateString) {
		this.state = state;
		this.resultStateString = resultStateString;
	}
	
	public PinState getState() {
		return state;
	}

	public String toResultStateString() {
		return resultStateString;
	}
}
