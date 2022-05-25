/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public interface TicketInterface extends Remote {								//Server's utilities
	
	public boolean signUp(String uname, String pass, String fname,				//Sign up
			String phone, String email, boolean admin) throws RemoteException;
	
	public boolean logIn(String uname, String pass) throws RemoteException;		//Log in
	
	public void logOut() throws RemoteException;								//Log out
	
	public boolean uRemove(String pass) throws RemoteException;					//Delete user

	public boolean adminUI() throws RemoteException;							//Check which UI to open (user's/admin's)

	public boolean addEvent(String title, String genre) throws RemoteException;	//Add event
	
	public boolean addPerformance(String title, LocalDate date, 				//Add performance
			LocalTime time, int seats, int price) throws RemoteException;

	public boolean eventExists(String title) throws RemoteException;			//Check event's existence by name

	public boolean deleteEvent(String title) throws RemoteException;			//Delete event

	public ArrayList<String> searchEvents(String genre) throws RemoteException; //Search all events by genre
	
	public String searchEvent(String title) throws RemoteException;				//Search for an event by title
	
	public float reserve(String title, LocalDate date,							//Reserve seats for performance
			LocalTime time, int seats) throws RemoteException;

	public void confirm(Boolean confirmed, String title, LocalDate date,		//Confirm purchase	
			LocalTime time) throws RemoteException;
} 