package main;

import java.io.IOException;
import java.nio.charset.Charset;

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
		builder.setEncoding(Charset.forName("UTF-8"));
		//builder.setEncoding(Charset.forName("ISO-8859-15"));
		//builder.setEncoding(Charset.forName("CP1252"));
		//builder.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true));
		builder.addListener(listener);
		PircBotX bot = new PircBotX(builder.buildConfiguration());
		System.out.println("Encoding : " + bot.getConfiguration().getEncoding());
		try {
			bot.startBot();
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}
	}	
}