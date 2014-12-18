package hu.droidium.remote_home_manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RaspberryTemperatureSensor {

	public static long measure() {
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
					return temp;
				}
			}
			return Long.MIN_VALUE;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Long.MIN_VALUE;
		} catch (IOException e) {
			e.printStackTrace();
			return Long.MIN_VALUE;
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
