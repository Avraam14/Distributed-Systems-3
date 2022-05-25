/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package clientui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ticketpackage.TicketInterface;

public class UserMenu {																//User's menu for reservations (also incomplete)
	public UserMenu(TicketInterface server) {	
		JFrame mainframe = new JFrame("Main Menu");
		JPanel contentPane = new JPanel();
		JButton search = new JButton("Search for Events");
		JButton logout = new JButton("Log Out");
		JButton delUser = new JButton("Delete Account");
		
		search.addActionListener(ActionEvent ->{									//Search for events to make reservations
			String[] choices = {"Genre", "Title"};									//By genre or title
			int choice = JOptionPane.showOptionDialog(contentPane,
					"Search by genre or title?", "Search",
					JOptionPane.INFORMATION_MESSAGE,
					JOptionPane.DEFAULT_OPTION, null,
					choices, choices[0]);
			if(choice == JOptionPane.CLOSED_OPTION) return;
			String answer;
			try {
				answer = JOptionPane.showInputDialog(contentPane,
						"Type genre or title of event(s) you are looking for");		//Insert keyword for genre/title
				ArrayList<String> events;											//The found events by the keyword
				if(choice == 0) {
					events = server.searchEvents(answer);							//By genre there can be many events
					if(events == null || events.isEmpty())
						JOptionPane.showMessageDialog(contentPane,
								"No events found in that genre");
					else
						new ReserveUI(events, server);								//Make reservations
				} else {
					String event = server.searchEvent(answer);						//By title only one event can be found
					if(event == null)
						JOptionPane.showMessageDialog(contentPane,
								"Event wasn't found by that title");
					else {
						events = new ArrayList<String>();							//Add it to the list
						events.add(event);
						new ReserveUI(events, server);								//Make reservations
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
										
		logout.addActionListener(ActionEvent ->{									//Log out
			if(JOptionPane.showConfirmDialog(contentPane,
					"Are you sure you want to log out?",
					"Logging Out", JOptionPane.YES_NO_OPTION)
					== JOptionPane.YES_OPTION) {									//Confirmations
				try {
					server.logOut();												//Done
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				new LogInUI(server);												//Next user can log in
				mainframe.dispose();
			}
		});
		
		delUser.addActionListener(ActionEvent ->{									//User deletes his account
			String confirmed = JOptionPane.showInputDialog(contentPane,
					"Confirm your password once more");								//Password confirmation
			try {
				if(server.uRemove(confirmed)) {
					JOptionPane.showMessageDialog(contentPane,						//Done
							"User deleted Successfully", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					new LogInUI(server);
					mainframe.dispose();
				}
				else
					JOptionPane.showMessageDialog(contentPane,
							"Couldn't delete the user", "No go",
							JOptionPane.INFORMATION_MESSAGE);
			} catch (HeadlessException | RemoteException e) {
				e.printStackTrace();
			}
		});
		
		contentPane.setLayout(new FlowLayout());
		contentPane.add(search);
		contentPane.add(logout);
		contentPane.add(delUser);
		mainframe.setContentPane(contentPane);									
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(new Dimension(500, 100));
		mainframe.setLocationRelativeTo(null);
		mainframe.setVisible(true);
	}
}