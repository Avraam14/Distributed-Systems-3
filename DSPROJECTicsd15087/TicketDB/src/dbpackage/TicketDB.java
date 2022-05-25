/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package dbpackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TicketDB {													//Database main
	public static void main(String[] args) {
		ObjectInputStream objin;
		ObjectOutputStream objout;
		while(true) {
			try {
				ServerSocket server = new ServerSocket(5555, 1);		//Socket connection with server
				System.out.println("Accepting Connection...");
				System.out.println("Local Address :" + server.getInetAddress() + 
						" Port :" + server.getLocalPort());
				Socket sock = server.accept();
				objout = new ObjectOutputStream(sock.getOutputStream());
				objin = new ObjectInputStream(sock.getInputStream());
				Message msg;
				EventManager em = new EventManager();					//Initialize event manager
				do {													//Protocol
					msg = null;
					do
						msg = (Message) objin.readObject();
					while(msg == null);
					em.putMessage(msg);
					objout.writeObject(em.getMessage());
				}while(!msg.getRequest().equals("stop"));
				objout.writeObject(new Message("done"));
				objin.close();
				objout.close();
				sock.close();
				server.close();
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}