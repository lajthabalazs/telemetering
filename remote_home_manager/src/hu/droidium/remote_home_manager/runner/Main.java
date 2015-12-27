package hu.droidium.remote_home_manager.runner;

import hu.droidium.remote_home_manager.HungarianLanguageModule;
import hu.droidium.remote_home_manager.RaspberryRelayController;
import hu.droidium.remote_home_manager.database.MySQLDataStore;
import hu.droidium.telemetering.interfaces.DatastoreBase;
import hu.droidium.telemetering.interfaces.LanguageInterface;
import hu.droidium.telemetering.interfaces.communication.Channel;
import hu.droidium.xmpp_chat.GoogleTalkClient;

import java.io.File;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
	public static void main(String[] args) {
		try {
			File configFile;
			if (args.length == 0) {
				configFile = new File("config.json");
				System.out.println("No config file, trying default " + configFile.getAbsolutePath());
			} else {
				configFile = new File(args[0]);
				System.out.println("Received config file " + configFile.getAbsolutePath());
			}
	
			if (configFile.exists()) {
				JsonFactory f = new JsonFactory();
				ObjectMapper mapper = new ObjectMapper();
				JsonParser parser;
				parser = f.createParser(configFile);
				JsonNode root = mapper.readTree(parser);
				JsonNode database = root.get("database");
				String url = database.get("url").asText();
				String databaseName = database.get("schema").asText();
				String dbUser = database.get("user").asText();
				String dbPassword = database.get("password").asText();
				String superUser = root.get("superuser").asText();
				
				JsonNode gtalk = root.get("gtalk");
				String gtalkUserName = gtalk.get("user").asText();
				String gtalkPassName = gtalk.get("password").asText();
	
				DatastoreBase datastore = new MySQLDataStore(url, databaseName, dbUser, dbPassword);
				datastore.addUser(superUser, true);
				
				LovasRaspberryModularSingleNode node = new LovasRaspberryModularSingleNode(false,
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
						Channel commClient = new GoogleTalkClient(gtalkUserName, gtalkPassName);
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
			} else {
				System.out.println("No config file, and default config not available at " + configFile.getAbsolutePath());
				System.exit(-1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
