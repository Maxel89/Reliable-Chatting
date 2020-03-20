package Reliable_Chatting;

import Reliable_Chatting.Client;
import Reliable_Chatting.Server;

public class Main {

	public static void main(String[] args) {
		Server serverInstance;
		String serverAddressString = "localhost";
		int serverPort = 1234;
		int clientPort = 5678;

		serverInstance = new Server(serverPort);
		serverInstance.start();

		String[] clientNames = {"Sara", "Kalle", "Anna", "Torsten"};
//		String[] clientNames = {"Sara"};
		Client[] clientInstances = new Client[clientNames.length];

		//creates the client, tells it to listen to the
		//clientport, sets the request message,
		//sets then parameters and then starts the server
		//this loop creates all the client instances
		for (int i = 0; i < clientNames.length; i++) {
			clientInstances[i] = new Client(clientNames[i], serverAddressString, serverPort, clientPort + i);
			clientInstances[i].start();
		}
	}
}
