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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ticketpackage.TicketInterface;

public class SignUpUI {														//Window in which the user signs up
	private static final String EMAIL_PATTERN = 							//Email pattern for user's email
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + 
	"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public SignUpUI(TicketInterface server) {				
		JFrame signframe = new JFrame("Sign Up");							//Main frame
		JPanel contentPane = new JPanel();									//Frame's content pane
		JPanel uname_pnl = new JPanel(new FlowLayout());					//Each field has its panel
		JPanel pass_pnl = new JPanel(new FlowLayout());
		JPanel pass2_pnl = new JPanel(new FlowLayout());
		JPanel email_pnl = new JPanel(new FlowLayout());
		JPanel fname_pnl = new JPanel(new FlowLayout());
		JPanel phone_pnl = new JPanel(new FlowLayout());
		JPanel admin_pnl = new JPanel(new FlowLayout());
		JButton signup = new JButton("Sign Up");							//Finish button
		JCheckBox admin_bttn = new JCheckBox("Admin:");						//Admin button
		JToggleButton show_pass = new JToggleButton("Show");				//Button to show password
		JToggleButton show_pass2 = new JToggleButton("Show");
		JLabel uname_lbl = new JLabel("User name:");						//Each field has its label
		JLabel pass_lbl = new JLabel("Password:");
		JLabel pass2_lbl = new JLabel("Confirm password:");
		JLabel fname_lbl = new JLabel("Full Name:");
		JLabel email_lbl = new JLabel("Email:");
		JLabel phone_lbl = new JLabel("Phone:");
		JTextField fname_txt = new JTextField(30);							//Text fields for the details
		JTextField email_txt = new JTextField(30);
		JTextField uname_txt = new JTextField(30);
		JTextField phone_txt = new JTextField(30);
		JPasswordField pass_txt = new JPasswordField(30);					//Password field to securely type in the password
		JPasswordField pass2_txt = new JPasswordField(30);					//Password field to confirm password
		
		pass_txt.setEchoChar('*');											//Set the character to be displayed in place of the letter as *
		pass2_txt.setEchoChar('*');
		
		show_pass.addActionListener(ActionEvent ->{							//If user chooses remove the echo character and show password
			if(show_pass.isSelected()) {
				pass_txt.setEchoChar((char) 0);
			} else {
				pass_txt.setEchoChar('*');
			}
		}); 
		
		show_pass2.addActionListener(ActionEvent ->{
			if(show_pass2.isSelected()) {
				pass2_txt.setEchoChar((char) 0);
			} else {
				pass2_txt.setEchoChar('*');
			}
		}); 
		
		signup.addActionListener(ActionEvent ->{							//Check if everything is okay before signing up
			if(uname_txt.getText().equals("") || 
					String.valueOf(pass_txt.getPassword()).isBlank() || 	//Check if any of the fields are empty
					String.valueOf(pass2_txt.getPassword()).isBlank() ||
					fname_txt.getText().isBlank() ||
					email_txt.getText().isBlank() ||
					phone_txt.getText().isBlank()) {
				JOptionPane.showMessageDialog(contentPane, 
						"Fill in all the fields.", "Bad Input",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (pass_txt.getPassword().length < 8 ){					//Check if the password isn't too short
				JOptionPane.showMessageDialog(contentPane,
						"Password is too short.", "Bad Input",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (!String.valueOf(pass_txt.getPassword()).equals(		//Check if the passwords match
					String.valueOf(pass2_txt.getPassword()))){
				JOptionPane.showMessageDialog(contentPane,
						"The passwords don't match.", "Bad Input",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (!email_txt.getText().matches(EMAIL_PATTERN)) {		//Check if email follows the pattern
				JOptionPane.showMessageDialog(contentPane, 
						"That's not a valid email.", "Bad Input",
						JOptionPane.INFORMATION_MESSAGE);
			} else {														//And if everything is alright
				try {
					if(server.signUp(uname_txt.getText(),					//Sign up if the user name is available
							String.valueOf(pass_txt.getPassword()), 
							email_txt.getText(), fname_txt.getText(),
							phone_txt.getText(), admin_bttn.isSelected())) {
						
						JOptionPane.showMessageDialog(contentPane, "User " 	//Success message
							+ uname_txt.getText() + " signed up successfully!",
							"Welcome!", JOptionPane.INFORMATION_MESSAGE);
						signframe.dispose();
					} else {
						JOptionPane.showMessageDialog(contentPane,			//User name is taken
								"Username already exists.", 
								"Username unavailable.", 
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (HeadlessException | RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		uname_pnl.add(uname_txt);											//Add every field to its panel
		pass_pnl.add(pass_txt);
		pass_pnl.add(show_pass);
		pass2_pnl.add(pass2_txt);
		pass2_pnl.add(show_pass2);
		email_pnl.add(email_txt);
		fname_pnl.add(fname_txt);
		phone_pnl.add(phone_txt);
		admin_pnl.add(admin_bttn);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(uname_lbl);											//Add every panel with its label to the content Pane
		contentPane.add(uname_pnl);
		contentPane.add(pass_lbl);
		contentPane.add(pass_pnl);
		contentPane.add(pass2_lbl);
		contentPane.add(pass2_pnl);
		contentPane.add(email_lbl);
		contentPane.add(email_pnl);
		contentPane.add(fname_lbl);
		contentPane.add(fname_pnl);
		contentPane.add(phone_lbl);
		contentPane.add(phone_pnl);
		contentPane.add(admin_pnl);
		contentPane.add(signup);
		uname_lbl.setAlignmentX(0.5f);										//Center everything
		pass_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		pass2_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		email_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		fname_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		phone_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		signup.setAlignmentX(Component.CENTER_ALIGNMENT);
		signframe.setSize(new Dimension(500, 450));							//Set the frame right
		signframe.setResizable(false);
		signframe.setContentPane(contentPane);
		signframe.setLocationRelativeTo(null);
		signframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		signframe.setVisible(true);
	}
}