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
import javax.swing.JTextField;

import ticketpackage.TicketInterface;

public class AddEventUI {															//UI for adding events
	public AddEventUI(TicketInterface server) {
		JFrame addframe = new JFrame("Add a new Event");
		JPanel contentPane = new JPanel();
		JPanel title_pnl = new JPanel(new FlowLayout());
		JPanel genre_pnl = new JPanel(new FlowLayout());
		JLabel title_lbl = new JLabel("Event's title");
		JLabel genre_lbl = new JLabel("Event's genre");
		JTextField title = new JTextField(30);
		JTextField genre = new JTextField(30);
		JButton done = new JButton("Continue");
		
		done.addActionListener(ActionEvent ->{
			if(title.getText().isBlank() ||											//Details can't be blank
					genre.getText().isBlank()) {
				JOptionPane.showMessageDialog(contentPane,
						"Fill in both fields");
			} else
				try {
					if(server.addEvent(title.getText(),								//Try creating an event
							genre.getText())) {
						JOptionPane.showMessageDialog(contentPane,
								"Event added successfully!");						//If no events exist with that name
						new AddPerformanceUI(server, title.getText());				//Add a performance
						addframe.dispose();
					}
					else {															//Can't have duplicate events
						JOptionPane.showMessageDialog(contentPane,
								"Event already exists");
					}
				} catch (HeadlessException | RemoteException e) {
					e.printStackTrace();
				}
		});
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		title_pnl.add(title);
		genre_pnl.add(genre);
		contentPane.add(title_lbl);
		contentPane.add(title_pnl);
		contentPane.add(genre_lbl);
		contentPane.add(genre_pnl);
		contentPane.add(done);
		title_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		genre_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		addframe.setSize(new Dimension(400, 200));										
		addframe.setResizable(false);
		addframe.setContentPane(contentPane);											
		addframe.setLocationRelativeTo(null);
		addframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addframe.setVisible(true);
	}
}