package Reliable_Chatting;

import java.net.InetAddress;

public class ClientData {
	private String name;
	private InetAddress address;
	private int port;

	public ClientData(String name, InetAddress address, int port) {
		this.name=name;
		this.address=address;
		this.port=port;
	}
	public InetAddress getAddress() {
		return address;
	}
	public String getName() {
		return name;
	}
	public int getPort() {
		return port;
	}
	public String toString() {
		String string = name + " " + address + ":" + port; 
		return string;
		
	}
}
