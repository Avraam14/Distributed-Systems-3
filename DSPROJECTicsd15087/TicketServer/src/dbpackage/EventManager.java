/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package dbpackage;

public class EventManager {										//Server's event manager
	private boolean hasresponse;								//Flag for response
	private Message request;									//Database's request
	private Object contents;									//Message contents
		
	public synchronized void putResponse(Message response) {	//Get response from db, synced with putRequest and getContents
		if(response.getContent()!=null)							//Because they manipulate the same objects once they're ready
			this.contents = response.getContent();
		else
			this.contents = null;
		hasresponse = true;
		notify();
	}
	
	public synchronized void putRequest(Message request) {		//Server's request to database
		this.request = request;									
		notify();												//Notify whichever method is waiting on the request
	}
	
	public synchronized Message getRequest() {					//Get request to send to database, once it's ready
		while(request == null)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Message msg = request;
		request = null;
		return msg;
	}

	public synchronized Object getContents() {					//Get the message contents from database
		while(!hasresponse)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		hasresponse = false;
		return contents;
	}
}