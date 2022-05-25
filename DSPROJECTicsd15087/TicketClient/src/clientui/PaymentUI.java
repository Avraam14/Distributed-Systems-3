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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import ticketpackage.TicketInterface;

public class PaymentUI {													//Final payment UI
	public PaymentUI(TicketInterface server, String title,
			LocalDate date, LocalTime time, int seats, float cost)
			throws ParseException {
		JFrame payframe = new JFrame("Pay your dues");
		JPanel contentPane = new JPanel();
		JPanel fname_pnl = new JPanel(new FlowLayout());
		JPanel card_pnl = new JPanel(new FlowLayout());
		JLabel fname_lbl = new JLabel("Full Name");							//Full name
		JLabel card_lbl = new JLabel("Card Number");						//Card number
		JTextField fname = new JTextField(30);
		JFormattedTextField cardno = new JFormattedTextField(
				new MaskFormatter("####-####-####-####"));					//Formatter for card
		JButton done = new JButton("Done");
		
		done.addActionListener(ActionEvent ->{								//Name and card number can't be blank
			if(fname.getText().isBlank() || cardno.getText().isBlank()) {
				JOptionPane.showMessageDialog(contentPane,
						"Fill in both fields");
			} else
				try {
					int confirm = JOptionPane.showConfirmDialog(contentPane,
							"Are you sure you want to pay " + cost +		//Confirmation
							" for " + seats + "?");
					server.confirm(confirm==JOptionPane.YES_OPTION, title,	//Done
							date, time);
					payframe.dispose();
				} catch (HeadlessException | RemoteException e) {
					e.printStackTrace();
				}
		});
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		fname_pnl.add(fname_lbl);
		card_pnl.add(card_lbl);
		contentPane.add(fname_pnl);
		contentPane.add(fname);
		contentPane.add(card_pnl);
		contentPane.add(cardno);
		contentPane.add(done);
		fname_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		card_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		payframe.setContentPane(contentPane);
		payframe.setSize(new Dimension(500, 400));
		payframe.setResizable(false);
		payframe.setLocationRelativeTo(null);
		payframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		payframe.setVisible(true);
	}
}