package udp_test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;

public class UDPClient {
	
	private DatagramSocket socket;

	public UDPClient(int port) {
		try {
			socket = new DatagramSocket(port);
			System.out.println("Address of server " + socket.getLocalAddress() + " : " + socket.getLocalPort());
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(true) {
				try {
					byte[] resp = "Hello server!".getBytes();
					packet = new DatagramPacket(resp, resp.length, Inet4Address.getLocalHost(), 999);
					socket.send(packet);					
					System.out.println("Waiting for packet on client");
					socket.receive(packet);
					System.out.println(new String(packet.getData()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new UDPClient(998);
	}
}