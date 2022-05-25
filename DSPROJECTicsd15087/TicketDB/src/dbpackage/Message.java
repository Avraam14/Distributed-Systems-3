/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package dbpackage;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{			//Message, protocol for server-db communication
	private String request;								//The request (code)
	private Object content;								//Anything needed to fulfill the request
	
	public Message(String request) {					//Simple message
		this.request = request;
		this.content = null;
	}
	
	public Message (String request, Object content) {	//More complex request
		this.request = request;
		this.content = content;
	}

	public String getRequest() {						//getters
		return request;
	}

	public Object getContent() {
		return content;
	}
}