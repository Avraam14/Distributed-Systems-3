/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package ticketpackage;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable{				//User 	
	private String uname;
	private String pass;
	private String fname;
	private String phone;
	private String email;
	private boolean admin;

	public User(String uname, String pass, String fname,
			String phone, String email, boolean admin) {
		this.uname = uname;
		this.pass = pass;
		this.fname = fname;
		this.phone = phone;
		this.email = email;
		this.admin = admin;
	}
	
	public boolean checkUname(String uname) {			//Check if user name exists
		return this.uname.equals(uname);
	}

	public boolean auth(String pass) {					//Authenticate user
		return this.pass.equals(pass);
	}

	@Override
	public boolean equals(Object o) {					//Override equals
		if(!(o instanceof User)) return false;
		User temp = (User) o;
		return this.uname.equals(temp.uname) && this.pass.equals(temp.pass) &&
				this.fname.equals(temp.fname) && this.email.equals(temp.email) &&
				this.phone.equals(temp.phone) && this.admin == temp.admin;
	}

	public boolean adminStatus() {						//Get admin status
		return admin;
	}
}