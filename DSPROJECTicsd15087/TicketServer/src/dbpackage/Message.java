package dbpackage;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private String request;
	private Object content;
	
	public Message(String request) {
		this.request = request;
		this.content = null;
	}
	
	public Message (String request, Object content) {
		this.request = request;
		this.content = content;
	}

	public String getRequest() {
		return request;
	}

	public Object getContent() {
		return content;
	}
}
