/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@SuppressWarnings("serial")
public class Performance implements Serializable{					//Performances
	private LocalDate date;											//Details
	private LocalTime time;
	private int seats;
	private int price;
	private int reserved;											//Reserved temporarily
	
	public Performance(LocalDate date, LocalTime time,
			int seats, int price) {
		this.date = date;
		this.time = time;
		this.seats = seats;
		this.price = price;
		this.reserved = 0;
	}

	public boolean checkDate(LocalDate date, LocalTime time) {		//Check if there is a performance at a date/time
		return this.date.equals(date) && this.time.equals(time);
	}
	
	@Override
	public String toString() {
		return "\nDate: " + date + "\nTime: " + time +				//toString for display
				"\nSeats Available: " + seats +
				"\nTicket Price: " + price + "\n";
	}

	public float reserve(int seats) {								//Reserve temporarily
		if(this.seats < seats) return -1;
		this.seats-=seats;
		this.reserved = seats;
		return seats * price;										//Calculate costs
	}
	
	public boolean confirm(boolean confirmed) {						//Finalize payment and booking
		if(confirmed) {
			reserved = 0;
			return seats < 10 && seats > 0;
		}
		else{
			seats += reserved;
			reserved = 0;
		}
		return false;
	}
	
	public boolean discount() {										//Apply discount
		this.price *= 0.6;
		return true;
	}
}