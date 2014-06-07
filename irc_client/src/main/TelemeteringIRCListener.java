package main;

import org.pircbotx.PircBotX;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class TelemeteringIRCListener extends ListenerAdapter<PircBotX> {

	@Override
	public void onGenericMessage(final GenericMessageEvent<PircBotX> event)
			throws Exception {
		// This way to handle commands is useful for listeners that listen for
		// multiple commands
		if (event.getMessage().startsWith("?hello")) {
			event.respond("Hello World!");
			return;
		}
		if (event.getMessage().equals("Milyen meleg van?")) {
			event.respond("Nagyon meleg van...");
			return;
		}
	}

	@Override
	public void onIncomingChatRequest(IncomingChatRequestEvent<PircBotX> event)
			throws Exception {
		ReceiveChat chat = event.accept();
		String line;
		while ((line = chat.readLine()) != null)
			if (line.equalsIgnoreCase("done")) {
				// Shut down the chat
				chat.close();
				break;
			} else {
				// Fun example
				int lineLength = line.length();
				chat.sendLine("Line '" + line + "' contains " + lineLength
						+ " characters");
			}
	}
}
