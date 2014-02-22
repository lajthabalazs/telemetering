package telemetering;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Main {
	public static void main(String[] args) {
		int measurementDelay = Integer.parseInt(args[0]);
		while (true) {
			try {
				Thread.sleep(measurementDelay * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BufferedReader input = null;
			try {
				input = new BufferedReader(new FileReader("/sys/bus/w1/devices/28-000004d11070/w1_slave"));
				String line = null;
				while ((line = input.readLine())!=null) {
					System.out.println("Read " + line);
					if (line.contains("t=")) {
						int tempStart = line.indexOf("t=") + 2;
						String tempString = line.substring(tempStart);
						long temp = Long.parseLong(tempString);
						tempString = (temp / 1000) + "." + (temp - 1000*(temp/1000)) + " C";
						System.out.println("Temp: " + tempString);
						postData(tempString);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private static void postData(String data) throws IOException {
		URL                 url;
	    URLConnection   urlConn;
	    DataOutputStream    printout;
	    DataInputStream     input;
	    // URL of CGI-Bin script.
	    url = new URL ("http://lovastelemetering.appspot.com/receivemeasurement");
	    // URL connection channel.
	    urlConn = url.openConnection();
	    // Let the run-time system (RTS) know that we want input.
	    urlConn.setDoInput (true);
	    // Let the RTS know that we want to do output.
	    urlConn.setDoOutput (true);
	    // No caching, we want the real thing.
	    urlConn.setUseCaches (false);
	    // Specify the content type.
	    urlConn.setRequestProperty
	    ("Content-Type", "application/x-www-form-urlencoded");
	    // Send POST output.
	    printout = new DataOutputStream (urlConn.getOutputStream ());
	    String content =
	    "deviceid=" + URLEncoder.encode ("28-000004d11070", "UTF-8") +
	    "&time=" + URLEncoder.encode ("" + System.currentTimeMillis(), "UTF-8") +
	    "&value=" + URLEncoder.encode (data, "UTF-8");
	    printout.writeBytes (content);
	    System.out.println ("Data sent");
	    printout.flush ();
	    printout.close ();
	    // Get response data.
	    input = new DataInputStream (urlConn.getInputStream ());
	    int dataLength = input.available();
	    byte[] dataBytes = new byte[dataLength];
	    input.read(dataBytes);
	    System.out.println ("Response received " + new String(dataBytes));
	    input.close ();		
	}
}