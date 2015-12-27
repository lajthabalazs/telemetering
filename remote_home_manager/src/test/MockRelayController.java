package test;

import hu.droidium.remote_home_manager.Relay;
import hu.droidium.remote_home_manager.RelayController;
import hu.droidium.remote_home_manager.RelayState;

public class MockRelayController implements RelayController {

	@Override
	public void setState(Relay relay, RelayState state) {
	}
}
