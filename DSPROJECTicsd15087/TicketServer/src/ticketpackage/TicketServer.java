/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import dbpackage.EventManager;
import dbpackage.Message;

public class TicketServer {

	public static void main(String[] args) {
		try (																	//Try-with-resources so that resources can be closed properly
				Socket sock = new Socket("localhost", 5555);					//The server's socket that will connect to the database
				ObjectOutputStream objout = new ObjectOutputStream(				//Stream for writing objects - sending messages
						sock.getOutputStream());			
				ObjectInputStream objin = new ObjectInputStream(				//Stream for reading objects - receiving messages
						sock.getInputStream());
				) {
			Message msg;
			EventManager em = new EventManager();								//Event manager to send request to the db

			try {
				TicketOperations ops = new TicketOperations(em);				//Remote object that the user is going to use
				Registry r = LocateRegistry.createRegistry(1099);				//Initialize
				Naming.rebind("//localhost/TicketServer", ops);
				System.out.println("Awaiting connection...");
			} catch (RemoteException | MalformedURLException e) {
				e.printStackTrace();
			}

			do {
				objout.writeObject(em.getRequest());							//Communication with the database
				msg = null;
				do
					msg = (Message) objin.readObject();
				while(msg == null);
				em.putResponse(msg);
			}while(!msg.getRequest().equals("done"));
		} catch (ConnectException e) {
			System.out.println("Database is offline");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}