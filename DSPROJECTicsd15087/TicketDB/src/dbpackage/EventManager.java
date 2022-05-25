/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package dbpackage;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import ticketpackage.Event;

public class EventManager {															//Database's event manager
	private final String eventFile = "events.dat";									//Event file
	private boolean hasresponse;													//Database has a response to the server
	private Object response;														//The contents of the response
	private Message toserver;														//The message for the server
	private Message request;														//The incoming request
		
	public void putMessage(Message request) {										//Server sends request
		this.request = request;
		respond();																	//Respond accordingly
	}

	public synchronized Message getMessage() {										//Get the message from the db once it's ready
		while(!hasresponse)															//Synchronized with respond which generates a response
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		hasresponse = false;
		return toserver;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized void respond() {											//Generate response
		while(request == null)														//Wait for a request
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		String code = request.getRequest();
		if(code.equals("ADD_EVENT")) {												//Server wants to add a new event
			ArrayList<String> params = 												
					(ArrayList<String>) request.getContent();						//Get contents of message which contain event's details
			response = addEvent(params.get(0), params.get(1));						//Generate the response						
		} else if (code.equals("ADD_PERF")) {										//Add performance
			ArrayList<Object> params =												//List of parameters needed
					(ArrayList<Object>) request.getContent();
			response = addPerformance((String) params.get(0),						//Generate response
					(LocalDate) params.get(1), (LocalTime) params.get(2),
					(int) params.get(3), (int) params.get(4));
		} else if (code.equals("DEL_EVENT")) {										//Delete event
			response = deleteEvent((String) request.getContent());	
		} else if (code.equals("EVENTS")) {											//Get events by genre
			response = searchEvents((String) request.getContent());
		} else if (code.equals("EVENT")) {											//Get event by title
			response = searchEvent((String) request.getContent());
		} else if (code.equals("RESERVE")) {										//Reserve seats
			ArrayList<Object> params =
					(ArrayList<Object>) request.getContent();						//Get needed parameters
			response = reserve((String) params.get(0),
					(LocalDate) params.get(1), (LocalTime) params.get(2),
					(int) params.get(3));
		} else if (code.equals("CONFIRM")) {										//Confirm payment
			ArrayList<Object> params = 
					(ArrayList<Object>) request.getContent();
			response = confirm((boolean) params.get(0),
					(String) params.get(1), (LocalDate) params.get(2),
					(LocalTime) params.get(3));
		} else if (code.equals("DISCOUNT")) {										//Make the discount
			ArrayList<Object> params =
					(ArrayList<Object>) request.getContent();
			response = discount((String) params.get(0),
					(LocalDate) params.get(1), (LocalTime) params.get(2));
		}
		toserver = new Message("RESPONSE", response);								//Put the message together
		hasresponse = true;															//Flag that response has been generated
		request = null;																//Request fulfilled
		notify();																	//Wake the other synced method
	}

	private Event getEvent(String name) {											//Get event by name
		File events = new File(eventFile);											//Event file
		if(!events.exists()) return null;											//No events written yet
		try( 
				ObjectInputStream ois = 
				new ObjectInputStream(new FileInputStream(events))){				//Input streams
			Event temp;
			while(true) {
				temp = (Event) ois.readObject();									//If object matches with the name
				if(temp.checkName(name))
					return temp;													//Return it
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean eventExists(String title) {										//If we can fetch an event it exists
		return getEvent(title) != null;
	}
		
	private boolean addEvent(String title, String genre) {							//Add a new event
		if(eventExists(title)) return false;										//Not if it exists
		File events = new File(eventFile);											//Event file
		try {
			ObjectOutputStream oos = null;
			if(events.createNewFile()) {
				oos = new ObjectOutputStream(
						new FileOutputStream(events));
			}
			else {
				oos = new ObjectOutputStream(
						new FileOutputStream(events, true)){
		            @Override
		            protected void writeStreamHeader() throws IOException{			//Reset the header to avoid corrupting the stream
		            	reset();
		            }
				};
			}
			oos.writeObject(new Event(title, genre));								//Write the new object
			oos.close();
			return true;															//Success
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return false;
	}
	
	private boolean addPerformance(String title, LocalDate date,					//Add performance
			LocalTime time, int seats, int price) {
		if(!eventExists(title)) return false;										//If title doesn't correspond to event it failed
		Event event = getEvent(title);												//Get the event to add performance
		deleteEvent(title);															//Delete the old event
		if(event.addPerformance(date, time, seats, price)) {						//Add the performance to the new event
			addEvent(event);														//Write the new event to save changes
			return true;
		}
		else return false;															//Unless there already is a performance that day/time
	}

	private void addEvent(Event event) {											//Add event by image of existing event
		File events = new File(eventFile);
		try {
			ObjectOutputStream oos = null;
			if(events.createNewFile()) {
				oos = new ObjectOutputStream(
						new FileOutputStream(events));
			}
			else {
				oos = new ObjectOutputStream(
						new FileOutputStream(events, true)){
		            @Override
		            protected void writeStreamHeader() throws IOException{			//Reset the header to avoid corrupting the stream
		            	reset();
		            }
				};
			}
			oos.writeObject(event);													//Simply write the event
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean deleteEvent(String title){										//Delete event by name
		Event todel = getEvent(title);												//Fetch it
		if(todel == null) return false;												//If there is no event it failed
		ArrayList<Event> events = new ArrayList<Event>();							//All events written
		File eventfile = new File(eventFile);										//Event file
		try (
				ObjectInputStream ois = new ObjectInputStream(						//Input-Output Streams
						new FileInputStream(eventfile));
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(eventfile));
				){
			while(true) {
				Event temp;
				try {
					temp = (Event) ois.readObject();
					if(!temp.equals(todel))											//Add all events to the list
						events.add(temp);											//Except the one we want deleted
				} catch (EOFException e) {
					break;
				}
			}

			for(Event event : events)
				oos.writeObject(event);												//Write all the events
				
			return true;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String searchEvent(String content) {									//Search event by name
		return eventExists(content) ? getEvent(content).toString() : null;			//If it exists fetch it if not return null
	}

	private ArrayList<String> searchEvents(String content) {						//Search events by genre
		ArrayList<String> events = new ArrayList<String>();							//All events in the genre
		File eventfile = new File(eventFile);										//Event file
		if(!eventfile.exists()) return null;										//There are no events written
		try( 
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(eventfile))){
			Event temp;
			while(true) {
				temp = (Event) ois.readObject();									//If the event is in the genre add it to the list
				if(temp.ofGenre(content))
					events.add(temp.toString());
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return events;																//return the list, empty or not
	}
	
	private float reserve(String title, LocalDate date,								//Reserve seats
			LocalTime time, int seats) {
		if(!eventExists(title)) return -1;											//There isn't an event by that title
		Event temp = getEvent(title);												//Fetch the event
		if(!temp.checkDate(date, time)) return -2;									//There is no performance at that day/time
		float cost = temp.bookDate(date, time, seats);								//Calculate cost
		deleteEvent(title);															//Save changes
		addEvent(temp);
		if(cost < 0) return -3;
		return cost;
	}

	private boolean confirm(boolean confirmed, String title,						//Confirmed purchase
			LocalDate date, LocalTime time) {
		boolean toreturn = getEvent(title).											//Calculate remaining seats (true if <10)
				getPerformance(date, time).confirm(confirmed);
		Event temp = getEvent(title);												//Save changes
		deleteEvent(title);
		addEvent(temp);
		return toreturn;
	}
	
	private boolean discount(String title, LocalDate date, LocalTime time) {		//Discount 40%
		Event temp = getEvent(title);
		boolean ok = temp.getPerformance(date, time).discount();					//Make the changes
		deleteEvent(title);															//Save the changes
		addEvent(temp);
		return ok;
	}
}