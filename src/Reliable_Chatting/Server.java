package Reliable_Chatting;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import Reliable_Chatting.EndPoint;

public class Server extends Thread{

	private EndPoint endPoint;
	private HashMap<String, ClientData> clients;
	private HashMap<Integer, Message> messages;
	private Semaphore clientsSemaphore;
	private boolean running = true;
	private Reliability_Handler reliabilityHandler;

	public Server (int serverPort) {
		// Create a communication channel
		clients = new HashMap<String, ClientData>();
		messages = new HashMap<Integer, Message>();
		endPoint = new EndPoint(serverPort);
		clientsSemaphore = new Semaphore(1);
		reliabilityHandler = new Reliability_Handler(messages);
	}
	public void run() {
		while (running == true) {
			// Receive a packet from client
			Message receivedMessage = endPoint.receivePacket();
			//			System.out.println("Server recived: \n" + receivedMessage.toString());

			if (reliabilityHandler.analyse(receivedMessage)!=null){
				if (receivedMessage.getStatus()==Message_Status.Recived) {
					sendMessage(receivedMessage, receivedMessage.getSender());
				}
				else if (receivedMessage.getStatus()==Message_Status.ACK) {
					if (receivedMessage.getReciver().contentEquals("Server")) {
						sendMessage(receivedMessage, receivedMessage.getSender());
					}
					else {
						sendMessage(receivedMessage, receivedMessage.getReciver());
					}
				}

				else if (receivedMessage.getContent().startsWith("/handshake") && receivedMessage.getStatus()==Message_Status.ACKII){
					addClient(receivedMessage);
				}
				else if (receivedMessage.getContent().startsWith("/tell") && receivedMessage.getStatus()==Message_Status.ACKII) {
					tellClient(receivedMessage);
				}
				else if (receivedMessage.getContent().startsWith("/list") && receivedMessage.getStatus()==Message_Status.ACKII) {
					listClients(receivedMessage);
				}
				else if (receivedMessage.getContent().startsWith("/tell") && receivedMessage.getStatus()==Message_Status.ACKII) {
					Message message = new Message("Server", receivedMessage.getSender(), "Incorrect command", generateID());
					sendMessage(message, receivedMessage.getSender());
				}
				else if (receivedMessage.getContent().startsWith("/leave") && receivedMessage.getStatus()==Message_Status.ACKII){
					leaveChat(receivedMessage);
				}
				else if (receivedMessage.getContent().startsWith("/") && receivedMessage.getStatus()==Message_Status.ACKII) {
					Message message = new Message("Server", receivedMessage.getSender(), "Incorrect command", generateID());
					sendMessage(message, message.getReciver());
				}
				else if (receivedMessage.getContent().contentEquals("You have left the chat") && receivedMessage.getStatus()==Message_Status.Act) {
					removeClient(receivedMessage.getReciver());
				}

				else if (receivedMessage.getStatus() == Message_Status.ACKII){
					broadcast(receivedMessage, receivedMessage.getSender());
					sendMessage(receivedMessage, receivedMessage.getSender());
				}
			}
		}
	}
	private void broadcast(Message message, String sender) {
		//		try {
		//			clientsSemaphore.acquire();
		for (Entry<String, ClientData> entry : clients.entrySet()) {
			if (!entry.getKey().contentEquals(sender)) {
				Message newMessage = new Message(message.getSender(), entry.getValue().getName(), message.getContent(), generateID());
				sendMessage(newMessage,newMessage.getReciver());
			}
		}
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		clientsSemaphore.release();
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
	public void sendMessage(Message message, String reciver) {
		// send the message
		InetAddress address = null;
		int port;
		if (clients.containsKey(reciver)) {
			address = clients.get(reciver).getAddress();
			port = clients.get(reciver).getPort();
		}
		else {
			address = message.getSenderAddress();
			port = message.getSenderPort();
		}

		if (messages.containsKey(message.getID())!=true) {
			messages.put(message.getID(), message);			
		}
		endPoint.sendPacket(message, address, port);
		//		Timeout timeout = new Timeout(message.getID(), 1000, messages, this);
		//		timeout.start();
	}

	private void addClient(Message receivedMessage) {
		//add client to the hashmap
		ClientData cd = new ClientData(receivedMessage.getSender(), receivedMessage.getSenderAddress(), receivedMessage.getSenderPort());
		//		try {
		//			clientsSemaphore.acquire();
		clients.put(cd.getName(), cd);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		clientsSemaphore.release();

		//generat a broadcast message to announce the new user
		Message broadcastMessage = new Message("Server", null, receivedMessage.getSender() + " has joined the chat", null);
		broadcast(broadcastMessage,receivedMessage.getSender());

		Message message = new Message("Server", receivedMessage.getSender(), "You have joined the chat", generateID());
		sendMessage(message, message.getReciver());
	}

	private void tellClient(Message receivedMessage) {
		// cut away "/tell" from the message
		// trim any leading spaces from the resulting messagrvere
		// split message into “recipient” name and the message
		String[] split = receivedMessage.getContent().split(" ", 3);
		Message message;
		//		try {
		//			clientsSemaphore.acquire();
		sendMessage(receivedMessage, receivedMessage.getSender());
		if (clients.containsKey(receivedMessage.getReciver())) {
			message = new Message(receivedMessage.getSender(), receivedMessage.getReciver(), split[2], generateID());		}
		else {
			message = new Message("Server", receivedMessage.getSender(), "No user by the name " + receivedMessage.getReciver(), generateID());
		}
		sendMessage(message, message.getReciver());

		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		clientsSemaphore.release();
	}

	private void listClients(Message receivedMessage) {
		String content = "\n" + "Active users:";
		//		try {
		//			clientsSemaphore.acquire();
		for (Entry<String, ClientData> entry : clients.entrySet()) {
			content = content + "\n \t" + entry.getKey();
		}
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		clientsSemaphore.release();
		Message message = new Message("Server", receivedMessage.getSender(), content, generateID());
		sendMessage(message, receivedMessage.getSender());
	}
	private void leaveChat(Message receivedMessage){
		Message broadcastMessage = new Message("Server", null, receivedMessage.getSender() + " has left the chat", null);
		broadcast(broadcastMessage,receivedMessage.getSender());
		Message message = new Message("Server", receivedMessage.getSender(), "You have left the chat", generateID());
		sendMessage(message, receivedMessage.getSender());
		sendMessage(receivedMessage, receivedMessage.getSender());
	}

	// remove sender name from chat members
	private void removeClient(String name) {
		clients.remove(name);
		if (clients.size()==0) {
			closeConnection();
		}
	}

	private void closeConnection() {
		endPoint.closeSocket();
		running=false;
	}

	public void resend(Message message, String receiver) {
		message.setID(generateID());
		sendMessage(message, receiver);
	}
}