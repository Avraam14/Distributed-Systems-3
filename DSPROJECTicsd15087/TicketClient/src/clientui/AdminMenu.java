/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package clientui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ticketpackage.TicketInterface;

public class AdminMenu {													//Admin menu for adding/deleting events

	public AdminMenu(TicketInterface server) {
		JFrame mainframe = new JFrame("Main Menu");
		JPanel contentPane = new JPanel();
		JButton addevent = new JButton("Add Event");
		JButton addperf = new JButton("Add Performance");
		JButton delevent = new JButton("Delete an Event");
		JButton logout = new JButton("Log Out");
		JButton delUser = new JButton("Delete Account");
		
		addevent.addActionListener(ActionEvent ->{							//Open UI for adding event
			new AddEventUI(server);
		});
		
		addperf.addActionListener(ActionEvent ->{							//Open UI for adding performance to event
			String title = JOptionPane.showInputDialog(contentPane,
					"Insert the title of the event to add a performance");
			if(title == null)
				return;
			try {
				if(server.eventExists(title))								//Check for event existence
					new AddPerformanceUI(server, title);					//Add the performance through UI
				else
					JOptionPane.showMessageDialog(contentPane,
							"There is no such event");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		delevent.addActionListener(ActionEvent ->{							//Delete event
			String title = JOptionPane.showInputDialog(contentPane,
					"Insert the title of the event to delete it");			//Get event's name
			try {
				if(server.deleteEvent(title))								//Check for event's existence then delete it
					JOptionPane.showMessageDialog(contentPane,	
							"Event deleted successfully");
				else
					JOptionPane.showMessageDialog(contentPane,
							"There is no such event");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		});
		
		logout.addActionListener(ActionEvent ->{							//Log out
			if(JOptionPane.showConfirmDialog(contentPane,
					"Are you sure you want to log out?",
					"Logging Out", JOptionPane.YES_NO_OPTION)
					== JOptionPane.YES_OPTION) {							//Confirmation
				try {
					server.logOut();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				new LogInUI(server);										//After log out another users logs in
				mainframe.dispose();
			}
		});
		
		delUser.addActionListener(ActionEvent ->{							//User deletes his account
			String confirmed = JOptionPane.showInputDialog(contentPane,
					"Confirm your password once more");						//Password confirmation
			try {
				if(server.uRemove(confirmed)) {								//Delete user
					JOptionPane.showMessageDialog(contentPane,
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
		contentPane.add(addevent);
		contentPane.add(addperf);
		contentPane.add(logout);
		contentPane.add(delUser);
		contentPane.add(delevent);
		mainframe.setContentPane(contentPane);									
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setSize(new Dimension(500, 100));
		mainframe.setLocationRelativeTo(null);
		mainframe.setVisible(true);
	}
}