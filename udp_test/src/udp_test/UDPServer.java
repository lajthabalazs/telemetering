package udp_test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
	
	private DatagramSocket socket;

	public UDPServer(int port) {
		try {
			socket = new DatagramSocket(port);
			System.out.println("Address of server " + socket.getLocalAddress() + " : " + socket.getLocalPort());
			System.out.println("Address of server " + socket.getLocalSocketAddress());
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(true) {
				try {
					System.out.println("Waiting for packet on server");
					socket.receive(packet);
					InetAddress clientAddress = packet.getAddress();
					int clientPort = packet.getPort();
					byte[] resp = "Hello to you too!".getBytes();
					packet = new DatagramPacket(resp, resp.length, clientAddress, clientPort);
					socket.send(packet);					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new UDPServer(999);
	}
}
