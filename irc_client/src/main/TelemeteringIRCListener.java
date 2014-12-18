package main;

import hu.droidium.telemetering.interfaces.LanguageInterface;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class TelemeteringIRCListener extends ListenerAdapter<PircBotX> {
	
	private LanguageInterface languageInterface;

	public TelemeteringIRCListener(LanguageInterface languageInterface) {
		this.languageInterface = languageInterface;
	}
	
	@Override
	public void onGenericMessage(final GenericMessageEvent<PircBotX> event) throws Exception {
		if (event instanceof PrivateMessageEvent) {
			String message = event.getMessage();
			System.out.println("Private message received: " + message);
			String response = languageInterface.getResponse(message, System.currentTimeMillis());
			if (response != null) {
				Thread.sleep(500);
				System.out.println("Responding: " + response);
				event.respond(response);
			}
		} else {
			System.out.println("Channel message received: " + event.getMessage());
		}
	}
	
	@Override
	public void onIncomingChatRequest(IncomingChatRequestEvent<PircBotX> event)
			throws Exception {
		event.accept();
	}
}
