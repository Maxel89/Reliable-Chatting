package Reliable_Chatting;

import java.util.HashMap;

public class Timeout extends Thread{
	private int id;
	private int time;
	private HashMap<Integer, Message> messages;
	private HashMap<String, ClientData> clients;
	private boolean running = true;
	private Client client;
	private Server server;

	public Timeout(Integer id, int time, HashMap<Integer, Message> messages, Client client) {
		this.id = id;
		this.time = time;
		this.messages = messages;
		this.client = client;
	}

	public Timeout(Integer id, int time, HashMap<Integer, Message> messages, Server server) {
		this.id = id;
		this.time = time;
		this.messages = messages;
		this.server = server;
	}

	private void resend() {
		if (client!=null) {
			client.resend(messages.get(id));
		}
		else if(server!=null) {
			server.resend(messages.get(id), clients.get(messages.get(id).getSender()).getAddress(), clients.get(messages.get(id).getSender()).getPort());
		}
		else {
			System.err.println("The timeout dosen't have a clienthandler nor a client");
		}
	}

	public void run() {
		while (running==true) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Reliable_Chatting.Message_Status status = messages.get(id).getStatus();
			if (status != Message_Status.ACKII && status != Message_Status.Act) {
				resend();
			}
			else if(status == Message_Status.Sent) {
				System.err.println("Message has not been sent yet");
				continue;
			}
			else if (status == Reliable_Chatting.Message_Status.ACK) {
				messages.remove(id);
				running=false;
			}
		}
	}

}
