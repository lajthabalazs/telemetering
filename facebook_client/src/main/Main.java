package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jodd.jerry.Jerry;
import jodd.jerry.JerryFunction;
import jodd.mutable.MutableInteger;

public class Main {

	private final static String EMAIL = "lovas.oreghegy.029.6@gmail.com";
	private final static String PASS = "Cicamica";

	public static void main(String[] args) throws IOException {
		Response response;

		response = FacebookUtils.loginToFacebook(EMAIL, PASS);
		FileWriter writer = new FileWriter(new File("e:\\tmp\\stuff\\facebook_login.html"));
		writer.write(response.getHtml());
		writer.flush();
		writer.close();
		
		response = FacebookUtils.findFriends(response);
		writer = new FileWriter(new File("e:\\tmp\\stuff\\facebook_find_friends.html"));
		writer.write(response.getHtml());
		writer.flush();
		writer.close();

		response = FacebookUtils.getMessages(response);
		writer = new FileWriter(new File("e:\\tmp\\stuff\\facebook_messages.html"));
		writer.write(response.getHtml());
		writer.flush();
		writer.close();
	}
}
