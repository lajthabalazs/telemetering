package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.GUIClient;
import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryRelayController;
import hu.droidium.remote_home_manager.database.SQLJetDatastore;
import hu.droidium.telemetering.interfaces.DatastoreBase;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.xmpp_chat.GoogleTalkClient;

public class Main {
	public static void main(String[] args) {
		String databaseFile;
		String userName;
		String password;
		String superUserId;
		boolean demoMode;
		boolean ui;
		System.out.println("Received " + args.length + " parameters.");
		if (args.length == 6) {
			System.out.println("Extracting parameters...");
			databaseFile = args[0];
			userName = args[1];
			password = args[2];
			superUserId = args[3];
			demoMode = Boolean.parseBoolean(args[4]);
			ui = false;
		} else {
			System.out.println("Using built in parameters...");
			databaseFile = "temp.sqlite";
			userName = "lovas_telemetering_client";
			password = "hello world";
			superUserId = "superUserId";
			demoMode = true;
			ui = true;
		}
		System.out.println("Database " + databaseFile);
		System.out.println("User " + userName);
		System.out.println("Password " + password);
		System.out.println("Demo mode " + demoMode);

		DatastoreBase datastore = new SQLJetDatastore(databaseFile);
		datastore.addUser(superUserId, true);
		
		LovasRaspberryModularSingleNode node = new LovasRaspberryModularSingleNode(demoMode,
				datastore,
				datastore,
				datastore,
				datastore,
				new RaspberryRelayController());
		node.run();		
		LanguageInterface languageInterface = new HungarianLanguageModule(node);
		node.registerListener(languageInterface);
		while (true) {
			try {
				Channel commClient = new GoogleTalkClient(userName, password);
				commClient.registerMessageListener(node);
				node.setLanguageInterface(commClient);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error " + e);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (ui) {
			new GUIClient(languageInterface);
		}
	}
}
