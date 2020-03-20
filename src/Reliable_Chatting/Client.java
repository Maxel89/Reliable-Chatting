package Reliable_Chatting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import Reliable_Chatting.EndPoint;

public class Client extends Thread implements ActionListener {

	private EndPoint clientEnd;
	private String name;
	private ChatGUI chatGUI;
	private boolean running = true;
	private HashMap<Integer, Message> messages;
	private Reliability_Handler reliabilityHandler;
	private InetAddress serverAddress;
	private int serverPortNumber;
	private int port;

	public Client(String name, String serverAddressString, int serverPortNumber, int clientPort) {
		// Create an endPoint on this computer to this
		// program identified by the provided port
		//Gives the client a name
		messages = new HashMap<Integer, Message>();
		reliabilityHandler = new Reliability_Handler(messages);
		this.serverPortNumber = serverPortNumber;
		this.port = clientPort;
		this.name = name;
		try {
			serverAddress = InetAddress.getByName(serverAddressString);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clientEnd = new EndPoint (port);
		// the GUI window from which the client message is picked up
		// Start up GUI (runs in its own thread)
		chatGUI = new ChatGUI(this, this.name);
	}
	public void actionPerformed(ActionEvent e) {
		// There is only one event coming out from the GUI and that’s
		// the carriage return in the text input field, which indicates the
		// message/command in the chat input field to be sent to the server

		String reciver = "Broadcast";
		Message message = null;


		// get the text typed in input field, using ChatGUI utility method
		String input = chatGUI.getInput();
		if (input.startsWith("/")) {
			if (input.startsWith("/handshake")) {
				reciver = "Server";
				try {
					message = new Message(name, reciver, input, generateID(), InetAddress.getByName("localhost"), port);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				sendMessage(message);
				// clear the GUI input field, using a utility function of ChatGUI
				chatGUI.clearInput();
			}
			//code continues next
			// Check whether it is a “tell” message
			else if (input.startsWith("/tell")) {
				String[] splitMessage = input.split(" ", 3);
				reciver = splitMessage[1];
				//System.out.println(input);
				//input = input.replace(reciver + " ", "");
				//System.out.println(input);
			}
			else if (input.startsWith("/list")) {
				reciver = "Server";
			}
			else if (input.startsWith("/leave")) {
				reciver = "Server";
			}

		}
		if (!chatGUI.getInput().contentEquals("")/* && input.contentEquals("/handshake")*/) {
			// add sender and reciver name to message

			message = new Message(name, reciver, input, generateID());

			sendMessage(message);
			// clear the GUI input field, using a utility function of ChatGUI
			chatGUI.clearInput();
		}
	}

	public void sendMessage(Message message) {
		// send the message
		if (messages.containsKey(message.getID())!=true) {
			messages.put(message.getID(), message);			
		}
		clientEnd.sendPacket(message, serverAddress, serverPortNumber);
	}

	public void resend(Message message) {
		if (message.getStatus() == Message_Status.Sent) {
			messages.remove(message.getID());
			message.setID(generateID());
			messages.put(message.getID(), message);
			sendMessage(message);
		}
	}
	private Integer generateID() {
		boolean notSet = true;
		Integer id = 0;
		while(notSet == true) {
			int min = 1000000;
			int max = 9999999;
			id =  (int) (Math.random() * ( max - min ) + min);
			if (!messages.containsKey(id)) {
				notSet = false;
			}
		}
		return id;
	}
	private void displayMessage(Message message, String sender) {
		chatGUI.displayMessage(sender + " - " + message.getContent());
	}

	public void run() {
		//performe a handshake
		Message message = null;
		try {
			message = new Message(name, "Server", "/handshake", generateID(), InetAddress.getByName("localhost"), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sendMessage(message);
		while(running==true){
			// Receive a message from server
			Message receivedMessage = clientEnd.receivePacket();

			if (reliabilityHandler.analyse(receivedMessage)!=null) {
				if (receivedMessage.getStatus()==Message_Status.Recived || receivedMessage.getStatus()==Message_Status.ACK) {
					sendMessage(receivedMessage);
				}
				else if (receivedMessage.getStatus()==Message_Status.ACKII) {
					sendMessage(receivedMessage);
					if (receivedMessage.getContent().contentEquals("You have left the chat")==true) {
						displayMessage(receivedMessage, receivedMessage.getSender());
						running=false;
						clientEnd.closeSocket();
						chatGUI.dispose();
					}
					else {
						displayMessage(receivedMessage, receivedMessage.getSender());
					}
				}
				else if (receivedMessage.getStatus()==Message_Status.Act) {
					String sender = "You";
					displayMessage(receivedMessage, sender);
				}
			}

		}
	}
}