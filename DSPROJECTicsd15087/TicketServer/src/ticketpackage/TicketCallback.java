/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicketCallback extends Remote{					//Interface for callbacks
	public void alert(String message) throws RemoteException;
}