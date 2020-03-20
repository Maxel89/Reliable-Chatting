package Reliable_Chatting;

import java.io.Serializable;
import java.net.InetAddress;

public class Message  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2518598978402153486L;
	private Integer id;
	private String sender;
	private String reciver;
	private String content;
	private Message_Status status = Message_Status.Sent;
	private InetAddress address;
	private int port;

	public Message(String sender, String reciver, String content, Integer id) {
		this.sender = sender;
		this.reciver = reciver;
		this.content = content;
		this.id = id;
	}
	public Message(String sender, String reciver, String content, Integer id, InetAddress address, int port) {
		this.sender = sender;
		this.reciver = reciver;
		this.content = content;
		this.id = id;
		this.address = address;
		this.port = port;
	}


	public int getID() {
		return id;
	}
	public void setID(Integer id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	public String getContent() {
		return content;
	}
	public Message_Status getStatus() {
		return status;
	}
	public void setStatus(Message_Status status) {
		this.status = status;
	}

	public InetAddress getSenderAddress() {
		return address;
	}
	public int getSenderPort() {
		return port;
	}

	public String toString() {
		return "ID: " + "\n \t" + id + "\n" + "\n" + "Status: " + "\n \t" + status + "\n" + "Sender: " + "\n \t" + sender + "\n" + "Reciver: " + "\n \t" + reciver + "\n" + "Content: " + "\n \t" + content;
	}
}
