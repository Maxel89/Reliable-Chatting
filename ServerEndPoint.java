package tcphandshake;

import java.io.*;
import java.net.*;
public class ServerEndPoint {
	ServerSocket socket = null;
	public ServerEndPoint(int port) {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public String readStream(Socket socket){
		String message = null;
		try {
			InputStream is = socket.getInputStream();
			DataInputStream din = new DataInputStream(is);
			message = din.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (message);
	}
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

// added try catch to the streams for error exception handling
