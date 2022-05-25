/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Event implements Serializable{								//The events
	private String title;												//Event title		
	private String genre;												//Event genre
	private ArrayList<Performance> events;								//Performances
	
	public Event(String title, String genre) {		
		this.title = title;
		this.genre = genre;
		this.events = new ArrayList<Performance>();
	}
	
	public boolean addPerformance(LocalDate date,						//Add a performance
			LocalTime time, int seats, int price) {
		if(checkDate(date, time)) return false;							//No duplicates
		events.add(new Performance(date, time, seats, price));			//Add it
		return true;
	}	

	public Performance getPerformance(LocalDate date, LocalTime time) {	//Get performance by date and time
		for(Performance p : events)
			if(p.checkDate(date, time))
				return p;
		return null;
	}
	
	public boolean checkDate(LocalDate date, LocalTime time) {			//Check if there is a performance that day
		return getPerformance(date, time) != null;
	}
	
	public float bookDate(LocalDate date, LocalTime time, int seats) {	//Book the dates temporarily
		Performance p = getPerformance(date, time);
		if(p == null) return -1;
		return p.reserve(seats);
	}
	
	public boolean confirm(boolean confirmed,							//Book permanently
			LocalDate date, LocalTime time) {		
		return getPerformance(date, time).confirm(confirmed);
	}
	
	public boolean checkName(String name) {								//Check if name is taken
		return this.title.equals(name);
	}

	public boolean ofGenre(String content) {							//Check genre
		return this.genre.equals(content);
	}
	
	@Override
	public String toString() {
		String toreturn = "Title: " + title +							//toString for display
				"\nGenre: " + genre + "\n  Performances: ";
		for(Performance p : events) {
			toreturn+=p.toString();
		}
		return toreturn;
	}
}