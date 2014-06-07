package main;

import java.io.IOException;

import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;

public class Main {
	public static void main(String[] args) {
		Builder<PircBotX> builder = new Builder<PircBotX>();
		builder.setName("lovas_telemetering_client");
		builder.setRealName("lovas_telemetering_client");
		builder.setServer("irc.chatjunkies.org", 6667);
		builder.setAutoNickChange(true);
		builder.setAutoReconnect(false);
		builder.addAutoJoinChannel("#xchat");
		builder.setCapEnabled(false);
		//builder.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true));
		builder.addListener(new TelemeteringIRCListener());
		PircBotX bot = new PircBotX(builder.buildConfiguration());
		try {
			bot.startBot();
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}
}