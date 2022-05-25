/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package clientui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ticketpackage.TicketInterface;

public class LogInUI {																	//Window through which the user logs in

	public LogInUI(TicketInterface server){
		JFrame logframe = new JFrame("Log in");											//Main frame
		JPanel contentPane = new JPanel();												//Frame's content pane
		JPanel uname_pnl = new JPanel(new FlowLayout());								//Each field has its panel
		JPanel pass_pnl = new JPanel(new FlowLayout());
		JPanel bttns = new JPanel(new FlowLayout());									//Container of the 2 buttons
		JButton login = new JButton("Log In");											//Buttons for logging in or signing up
		JButton signup = new JButton("Sign Up");
		JToggleButton show_pass = new JToggleButton("Show");							//Button to show password
		JLabel uname_lbl = new JLabel("User name:");									//Labels explaining the text fields
		JLabel pass_lbl = new JLabel("Password:");
		JTextField uname_txt = new JTextField(30);										//User name goes here
		JPasswordField pass_txt = new JPasswordField(30);								//Password goes here
		
		pass_txt.setEchoChar('*');														//This character will be shown instead of password
																						//letters
		show_pass.addActionListener(ActionEvent ->{										
			if(show_pass.isSelected()) {
				pass_txt.setEchoChar((char) 0);											//No char is selected so the password is shown
			} else {
				pass_txt.setEchoChar('*');
			}
		}); 
		
		login.addActionListener(ActionEvent ->{
			if(uname_txt.getText().equals("") || 										//If every field is acceptable we attempt log in
					String.valueOf(pass_txt.getPassword()).equals("")) {
				JOptionPane.showMessageDialog(contentPane, "Fill in both fields.",
						"Bad Input", JOptionPane.INFORMATION_MESSAGE);
			} else if (pass_txt.getPassword().length < 8 ){		
				JOptionPane.showMessageDialog(contentPane, "Password too short.",
						"Bad Input", JOptionPane.INFORMATION_MESSAGE);
			} else {
				try {
					if(server.logIn(uname_txt.getText(), 								//If successful we open up a menu and close the window
							String.valueOf(pass_txt.getPassword()))) {
						JOptionPane.showMessageDialog(contentPane, "Log in successful!",
								"Welcome!", JOptionPane.INFORMATION_MESSAGE);
						if(server.adminUI()) new AdminMenu(server);
						else new UserMenu(server);
						logframe.dispose();
					} else {															//Else we wait for proper credentials
						JOptionPane.showMessageDialog(contentPane,
								"Username or password is incorrect.", "Wrong credentials",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (HeadlessException | RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		signup.addActionListener(ActionEvent ->{										//If user has no account yet they can sign up
			new SignUpUI(server);
		});
		
		uname_pnl.add(uname_txt);														//Add each field to its panel
		pass_pnl.add(pass_txt);
		pass_pnl.add(show_pass);
		bttns.add(login);
		bttns.add(signup);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));			//Add each label with its panel to the content pane
		contentPane.add(uname_lbl);
		contentPane.add(uname_pnl);
		contentPane.add(pass_lbl);
		contentPane.add(pass_pnl);
		contentPane.add(bttns);	
		uname_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);							//Center everything
		pass_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		logframe.setSize(new Dimension(500, 250));										//Set the content pane
		logframe.setResizable(false);
		logframe.setContentPane(contentPane);
		logframe.setLocationRelativeTo(null);
		logframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		logframe.setVisible(true);
	}
}