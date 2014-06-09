package main;

import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.LanguageInterface;
import hu.droidium.remote_home_manager.SQLJetDatastore;

import java.io.IOException;

import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

public class TelemeteringIRCClient {
	
	public TelemeteringIRCClient(String name, String server, int serverPort, String channel, TelemeteringIRCListener listener) {
		Builder<PircBotX> builder = new Builder<PircBotX>();
		builder.setName(name);
		builder.setRealName(name);
		builder.setServer(server, serverPort);
		builder.setAutoNickChange(true);
		builder.setAutoReconnect(false);
		builder.addAutoJoinChannel(channel);
		builder.setCapEnabled(false);
		//builder.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true));
		builder.addListener(listener);
		PircBotX bot = new PircBotX(builder.buildConfiguration());
		try {
			bot.startBot();
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SQLJetDatastore datastore = new SQLJetDatastore("temp.sqlite");
		LanguageInterface languageInterface = new HungarianLanguageModule(datastore);
		TelemeteringIRCListener listener = new TelemeteringIRCListener(languageInterface);
		new TelemeteringIRCClient("lovas_telemetering_client", "irc.chatjunkies.org", 6667, "#xchat", listener);
	}
}