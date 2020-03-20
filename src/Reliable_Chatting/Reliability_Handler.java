package Reliable_Chatting;

import java.util.HashMap;

public class Reliability_Handler {

	private HashMap<Integer, Message> messages;

	public Reliability_Handler(HashMap<Integer, Message> messages) {
		this.messages=messages;
	}

	public Message analyse(Message message) {
		if (message.getStatus()==Message_Status.Sent) {
			message.setStatus(Message_Status.Recived);
			messages.put(message.getID(), message);
			return message;
		}
		else if (messages.containsKey(message.getID())==true && message.getStatus()!=Message_Status.Sent) {

			if (message.getStatus()==Message_Status.Recived) {
				message.setStatus(Message_Status.ACK);
				messages.replace(message.getID(), message);
			}
			else if (message.getStatus()==Message_Status.ACK) {
				message.setStatus(Message_Status.ACKII);
				messages.remove(message.getID());
			}
			else if (message.getStatus()==Message_Status.ACKII) {
				message.setStatus(Message_Status.Act);
				messages.remove(message.getID());
				return message;
			}
			return message;
		}
		else {
			return null;
		}

	}

}
