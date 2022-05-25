/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package clientui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DateFormatter;

import ticketpackage.TicketInterface;

public class ReserveUI {

	public ReserveUI(ArrayList<String> events,  TicketInterface server) {
		JFrame mainframe = new JFrame("Reserve Seats");							//Main frame
		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel reserve_pnl = new JPanel();
		JPanel print_pnl = new JPanel();										//Panel containing all the events
		JScrollPane scrPane = new JScrollPane(print_pnl);						//Scroll pane containing said panel
		JButton done = new JButton("Done.");									//Finish button
		JPanel[] event_pnl = new JPanel[events.size()];							//Every are will have its panel for aesthetic reasons
		JTextArea[] event_txt = new JTextArea[events.size()];					//The text areas with the events

		NumberFormat seatformat = NumberFormat.getIntegerInstance();			//Seats are in number format
		seatformat.setMaximumFractionDigits(0);
		seatformat.setGroupingUsed(false);
		JPanel title_pnl = new JPanel(new FlowLayout());
		JLabel title_lbl = new JLabel("Event's title");
		JPanel date_pnl = new JPanel(new FlowLayout());
		JPanel time_pnl = new JPanel(new FlowLayout());
		JPanel seats_pnl = new JPanel(new FlowLayout());
		JLabel date_lbl = new JLabel("Event date");
		JLabel time_lbl = new JLabel("Event starting time");
		JLabel seats_lbl = new JLabel("Seats: ");
		JTextField title = new JTextField(30);
		JFormattedTextField date = new JFormattedTextField(						//Every field is formatted for safe results
				new DateFormatter(new SimpleDateFormat("dd/mm/yyyy")));
		JFormattedTextField time = new JFormattedTextField(
				new DateFormatter(new SimpleDateFormat("HH:mm")));
		JFormattedTextField seats = new JFormattedTextField(seatformat);
		
		print_pnl.setLayout(new BoxLayout(print_pnl, BoxLayout.Y_AXIS));
		
		for(int i = 0; i < events.size(); i++) {
			event_txt[i] = new JTextArea(events.get(i).toString());				//initialize each area with an event
			event_txt[i].setEditable(false);									//user can't edit this from here
			event_pnl[i] = new JPanel(new FlowLayout());						//initializing the panels
			event_pnl[i].add(event_txt[i]);										//adding the area to the panel
			print_pnl.add(event_pnl[i]);										//adding the panel to the container
		}
		
		done.addActionListener(ActionEvent ->{
			if(date.getText().isBlank() || time.getText().isBlank() ||			//No blank details
					seats.getText().isBlank()){
				JOptionPane.showMessageDialog(contentPane,
						"Fill in all the fields");
			}
			else {
				int seatsnum = Integer.parseInt(seats.getText());				//Parse the values
				if(seatsnum < 0) {
					JOptionPane.showMessageDialog(contentPane,
							"Bad input for price");
				}
				else {
					try {
						LocalDate ldate = LocalDate.parse(date.getText(), 
								DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						LocalTime ltime = LocalTime.parse(time.getText(),
								DateTimeFormatter.ofPattern("HH:mm"));
						float result = server.reserve(title.getText(),			//Compute cost (and reserve the chairs)
								ldate, ltime, seatsnum);
						if(result >= 0) {										//result >= 0 means that no problems occurred
							JOptionPane.showMessageDialog(contentPane,
									"The seats are reserved! "
									+ "Redirecting to the payment screen...");
							new PaymentUI(server, title.getText(), ldate,		//Continue with final payment
									ltime, seatsnum, result);
							mainframe.dispose();
						} else if (result == -1){								//Errors
							JOptionPane.showMessageDialog(contentPane,
									"No events exist with that title");
						} else if (result == -2) {
							JOptionPane.showMessageDialog(contentPane,
									"No performance exists in that date/time");
						} else {
							JOptionPane.showMessageDialog(contentPane,
									"You ordered too many seats");
						}
					} catch (DateTimeParseException e) {
						JOptionPane.showMessageDialog(contentPane,
								"Bad input for Date/Time");
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		print_pnl.add(done);												
		reserve_pnl.setLayout(new BoxLayout(reserve_pnl, BoxLayout.Y_AXIS));
		title_pnl.add(title_lbl);
		date_pnl.add(date_lbl);
		time_pnl.add(time_lbl);
		seats_pnl.add(seats_lbl);
		reserve_pnl.add(title_pnl);
		reserve_pnl.add(title);
		reserve_pnl.add(date_pnl);
		reserve_pnl.add(date);
		reserve_pnl.add(time_pnl);
		reserve_pnl.add(time);
		reserve_pnl.add(seats_pnl);
		reserve_pnl.add(seats);
		title_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		date_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		time_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		seats_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainframe.setContentPane(contentPane);
		mainframe.getContentPane().add(scrPane, BorderLayout.WEST);					//Half a screen for event display
		mainframe.getContentPane().add(reserve_pnl, BorderLayout.EAST);				//And half for reservations
		mainframe.setSize(new Dimension(500, 500));								
		mainframe.setResizable(false);
		mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainframe.setLocationRelativeTo(null);
		mainframe.setVisible(true);
	}
}