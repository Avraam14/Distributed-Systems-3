/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import clientui.LogInUI;

@SuppressWarnings("serial")
public class TicketClient extends UnicastRemoteObject				//Client is a remote object for callbacks
	implements TicketCallback {

	protected TicketClient() throws RemoteException {
		super();
	}

	public static void main(String[] args) {
		String name = "//localhost/TicketServer";					//Server's name
		try {
			TicketClient client = new TicketClient();				//Remote object for callbacks
			Registry r = LocateRegistry.createRegistry(1098);		//Assign to a port
			Naming.rebind("//localhost/TicketClient", client);		//Set naming
			TicketInterface server = 
					(TicketInterface) Naming.lookup(name);			//Initialize the server's remote object
			new LogInUI(server);									//User logs in
		} catch (ConnectException e) {
			System.out.println("Server is offline");
		} catch (MalformedURLException | 
				RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void alert(String message) throws RemoteException {		//The callback
		JOptionPane.showMessageDialog(null, message);
	}
}