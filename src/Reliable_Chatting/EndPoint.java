package Reliable_Chatting;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EndPoint {
	//	private String payloadString;
	//private InetAddress address; // this endpoint address
	private int portNumber; // the program port number used through this endpoint
	private DatagramSocket socket; // the communication socket of this endpoint

	public EndPoint (int m_portNumber) {
		portNumber = m_portNumber;
		try { //only this exception is handled for illustration!
			//address = InetAddress. getLocalHost ();
			socket = new DatagramSocket(portNumber); }
		catch (Exception e) {
			System.err.println("Error creating endPoint!"); }
	}
	public void sendPacket(Message message, InetAddress destinationAddress, int destPortNumber) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			oos.flush();
			// get the byte array of the object
			byte[] buf= baos.toByteArray();

			// now send the payload
			DatagramPacket packet = new DatagramPacket(buf, buf.length, destinationAddress, destPortNumber);
			socket.send(packet);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Message receivePacket() {
		Message message = null;

		try {
//			byte[] data = new byte[4];
//			DatagramPacket packet = new DatagramPacket(data, data.length );
//			socket.receive(packet);

//			int len = 0;
//			// byte[] -> int
//			for (int i = 0; i < 4; ++i) {
//				len |= (data[3-i] & 0xff) << (i << 3);
//			}

			// now we know the length of the payload
			byte[] buffer = new byte[65536];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length );
			socket.receive(packet);

			ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
			ObjectInputStream oos = new ObjectInputStream(baos);
			message = (Message)oos.readObject();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return message;
	}

	public void closeSocket() {
		socket.close();
	}
}