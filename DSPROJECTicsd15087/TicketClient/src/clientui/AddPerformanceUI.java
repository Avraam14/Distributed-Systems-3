/*
 * Avraam Katsigras 321/2015087
 * Antonios Pittas 321/2017158
 */

package clientui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.DateFormatter;

import ticketpackage.TicketInterface;

public class AddPerformanceUI {														//UI for adding performances to an event
	public AddPerformanceUI(TicketInterface server, String title) {
		NumberFormat seatformat = NumberFormat.getIntegerInstance();				//Seats should be int
		seatformat.setMaximumFractionDigits(0);
		seatformat.setGroupingUsed(false);											//Disable grouping (1000 instead of 1,000)
		NumberFormat priceformat = NumberFormat.getNumberInstance();				//Price can be float
		priceformat.setMaximumFractionDigits(2);									//With 2 fraction digits
		priceformat.setGroupingUsed(false);
		
		JFrame addframe = new JFrame("Add Perfomance to an Event");
		JPanel contentPane = new JPanel();
		JPanel date_pnl = new JPanel(new FlowLayout());
		JPanel time_pnl = new JPanel(new FlowLayout());
		JPanel seats_pnl = new JPanel(new FlowLayout());
		JPanel price_pnl = new JPanel(new FlowLayout());
		JLabel date_lbl = new JLabel("Event date");
		JLabel time_lbl = new JLabel("Event starting time");
		JLabel seats_lbl = new JLabel("Maximum seat capacity");
		JLabel price_lbl = new JLabel("Ticket cost");
		JFormattedTextField date = new JFormattedTextField(
				new DateFormatter(new SimpleDateFormat("dd/mm/yyyy")));				//Every value needs to be formatted
		JFormattedTextField time = new JFormattedTextField(
				new DateFormatter(new SimpleDateFormat("HH:mm")));
		JFormattedTextField seats = new JFormattedTextField(seatformat);
		JFormattedTextField price = new JFormattedTextField(priceformat);
		JButton done = new JButton("Done");
		
		done.addActionListener(ActionEvent ->{
			if(date.getText().isBlank() || time.getText().isBlank() ||				//Can't have blank details
					seats.getText().isBlank() || price.getText().isBlank()){
				JOptionPane.showMessageDialog(contentPane,
						"Fill in all the fields");
			}
			else {
				int seatsnum = Integer.parseInt(seats.getText());					//Parse the numbers with check
				int pricenum = Integer.parseInt(price.getText());
				if(seatsnum < 0 || pricenum < 0) {
					JOptionPane.showMessageDialog(contentPane,
							"Bad input for seats or price");
				}
				else {
					try {
						LocalDate ldate = LocalDate.parse(date.getText(), 			//Parse the dates
								DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						LocalTime ltime = LocalTime.parse(time.getText(),
								DateTimeFormatter.ofPattern("HH:mm"));
						if(server.addPerformance(title, ldate, ltime,				//Upon adding performance
								seatsnum, pricenum)) {								//Check for duplicate date/time
							JOptionPane.showMessageDialog(contentPane,
									"Added a new performance to " + title);
							addframe.dispose();
						}
						else {
							JOptionPane.showMessageDialog(contentPane,
									"A performance already exists for that date/time");
						}
					} catch (DateTimeParseException e) {
						JOptionPane.showMessageDialog(contentPane,
								"Bad input for Date/Time");
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		date_pnl.add(date_lbl);
		time_pnl.add(time_lbl);
		seats_pnl.add(seats_lbl);
		price_pnl.add(price_lbl);
		contentPane.add(date_pnl);
		contentPane.add(date);
		contentPane.add(time_pnl);
		contentPane.add(time);
		contentPane.add(seats_pnl);
		contentPane.add(seats);
		contentPane.add(price_pnl);
		contentPane.add(price);
		contentPane.add(done);
		date_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		time_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		seats_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		price_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		addframe.setSize(new Dimension(500, 500));										
		addframe.setResizable(false);
		addframe.setContentPane(contentPane);											
		addframe.setLocationRelativeTo(null);
		addframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addframe.setVisible(true);
	}
}