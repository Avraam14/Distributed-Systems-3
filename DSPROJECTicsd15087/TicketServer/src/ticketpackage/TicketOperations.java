/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import dbpackage.EventManager;
import dbpackage.Message;

@SuppressWarnings("serial")
public class TicketOperations extends UnicastRemoteObject							//The remote object used by the user
		implements TicketInterface {
	private EventManager em;														//Event manager to send messages to the db	
	private final String userFile = "users.dat";									//User file
	private User currentUser;														//Current user
	private TicketCallback client;													//Callback client
	
	public TicketOperations(EventManager em) throws RemoteException {				
		super();
		this.em = em;
	}

	private User getByUname(String name) {											//Get user by name
		File usrfile = new File(userFile);											//Search the user file
		try (
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(usrfile));
				){
			User temp;
			while(true) {
				temp = (User) ois.readObject();
				if(temp.checkUname(name))
					return temp;
			}
		} catch (FileNotFoundException | EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean signUp(String uname, String pass, String fname,					//Sign a user up
			String phone, String email, boolean admin) 
					throws RemoteException {
		if(getByUname(uname) != null) return false;									//If user name doesn't exist
		File usrfile = new File(userFile);											//Write the new user to the file
		ObjectOutputStream out = null;
		try {
			if(!usrfile.createNewFile()) {											//If user file exists we open it for appending
				out = new ObjectOutputStream(
						new FileOutputStream(usrfile, true)){
		            @Override
		            protected void writeStreamHeader() throws IOException{			//Reset the header to avoid corrupting the stream
		            	reset();
		            }
				};
			} else {
				out = new ObjectOutputStream(
						new FileOutputStream(usrfile));								//If it didn't exist we just write over it
			}
			out.writeObject(
					new User(uname, pass, fname, phone, email, admin));
			out.close();
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	@Override
	public boolean logIn(String uname, String pass) throws RemoteException {		//Log a user in
		User toAuth = getByUname(uname);
		if(toAuth == null || !toAuth.auth(pass)) return false;						//Authenticate user
		currentUser = toAuth;														//Done
		
		String name = "//localhost/TicketClient";									//Initialize the remote callback object
		try {
			client = (TicketCallback) Naming.lookup(name);
		} catch (MalformedURLException |
				RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean uRemove(String pass) {											//Delete a user
		if(!currentUser.auth(pass)) return false;									//Wrong password
		ArrayList<User> users = new ArrayList<User>();								//All the users written
		File usrfile = new File(userFile);											//User file
		try (
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(usrfile));
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(usrfile));
				){
			while(true) {
				User temp;
				try {
					temp = (User) ois.readObject();
					if(!temp.equals(currentUser))									//Write all the users leaving the one we want out
						users.add(temp);
				} catch (EOFException e) {
					break;
				}
			}

			for(User user : users)
				oos.writeObject(user);
			
			return true;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean adminUI() throws RemoteException {								//Check if user is admin to present proper UI
		return currentUser.adminStatus();
	}

	@Override
	public void logOut() throws RemoteException {									//User logs out
		currentUser = null;
	}

	@Override
	public boolean addEvent(String title, String genre)								//Add event
			throws RemoteException {
		ArrayList<String> params = new ArrayList<String>();							//Set the parameters
		params.add(title);
		params.add(genre);
		em.putRequest(new Message("ADD_EVENT", params));							//Send the message
		return (boolean) em.getContents();											//Recieve the message
	}

	@Override
	public boolean addPerformance(String title, LocalDate date,						//Add performance to event
			LocalTime time, int seats, int price)
			throws RemoteException {
		ArrayList<Object> params = new ArrayList<>();								//Set the parameters
		params.add(title);
		params.add(date);
		params.add(time);
		params.add(seats);
		params.add(price);
		em.putRequest(new Message("ADD_PERF", params));								//Send the request
		return (boolean) em.getContents();											//Receive the reply
	}

	@Override
	public boolean eventExists(String title) throws RemoteException {				//Check if event exists
		em.putRequest(new Message("CHCK_NAME", title));								//Send request
		return (boolean) em.getContents();											//Get reply
	}
	
	@Override
	public boolean deleteEvent(String title) throws RemoteException {				//Delete event
		em.putRequest(new Message("DEL_EVENT", title));	
		return (boolean) em.getContents();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> searchEvents(String genre)								//Search events by genre
			throws RemoteException {
		em.putRequest(new Message("EVENTS", genre));
		return (ArrayList<String>) em.getContents();
	}

	@Override
	public String searchEvent(String title) throws RemoteException {				//Search event by title
		em.putRequest(new Message("EVENT", title));
		return (String) em.getContents();
	}

	@Override
	public float reserve(String title, LocalDate date,								//Reserve seats
			LocalTime time, int seats) throws RemoteException{
		ArrayList<Object> params = new ArrayList<>();
		params.add(title);
		params.add(date);
		params.add(time);
		params.add(seats);
		em.putRequest(new Message("RESERVE", params));
		return (float) em.getContents();
	}

	private void discount(String title, LocalDate date, LocalTime time) {			//Apply discount
		ArrayList<Object> params = new ArrayList<>();
		params.add(title);
		params.add(date);
		params.add(time);
		em.putRequest(new Message("DISCOUNT", params));
		em.getContents();
	}

	@Override
	public void confirm(Boolean confirmed, String title, LocalDate date,			//Confirm purchase
			LocalTime time) throws RemoteException {
		ArrayList<Object> params = new ArrayList<>();
		params.add(confirmed);
		params.add(title);
		params.add(date);
		params.add(time);
		em.putRequest(new Message("CONFIRM", params));
		if((boolean) em.getContents() && 											//If less than 10 tickets remain
				!LocalDate.now().getDayOfWeek().equals(DayOfWeek.SATURDAY) &&		//And it's not the weekend
				!LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			client.alert("For " + title + " at: " + date + ", " + time +			//We let every user know!
				"tickets are 40% off!!!");
			discount(title, date, time);											//Apply discount
		}
	}
}