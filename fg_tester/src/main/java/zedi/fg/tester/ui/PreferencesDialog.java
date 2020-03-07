package zedi.fg.tester.ui;

import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import zedi.pacbridge.utl.StringUtilities;
import zedi.pacbridge.utl.Utilities;

import javax.swing.JTextField;

public class PreferencesDialog extends JDialog
{
	private JTextField ipAddressTextField;
	private JTextField portNumberTextField;
	
	public PreferencesDialog() {
		setTitle("Preferences");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);		
		getContentPane().setLayout(null);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!Utilities.isValidIpAddress(ipAddressTextField.getText()))
				{
                    JOptionPane.showMessageDialog(PreferencesDialog.this, "Invalid IP address", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                    ipAddressTextField.requestFocusInWindow();
				 } else if (StringUtilities.isNumericString(portNumberTextField.getText()) == false) {
					 JOptionPane.showMessageDialog(PreferencesDialog.this, "Invalid port", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
					 portNumberTextField.requestFocusInWindow();
				 } else { 				
					 dispose();
				 }
			}
		});
		okButton.setBounds(75, 144, 114, 25);
		getContentPane().add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		cancelButton.setBounds(201, 144, 114, 25);
		getContentPane().add(cancelButton);
		
		JLabel lblNewLabel = new JLabel("Address:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(40, 37, 66, 15);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Port:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(40, 67, 66, 15);
		getContentPane().add(lblNewLabel_1);
		
		ipAddressTextField = new JTextField();
		ipAddressTextField.setBounds(109, 35, 124, 19);
		getContentPane().add(ipAddressTextField);
		ipAddressTextField.setColumns(10);
		
		portNumberTextField = new JTextField();
		portNumberTextField.setBounds(109, 65, 66, 19);
		getContentPane().add(portNumberTextField);
		portNumberTextField.setColumns(10);
	}
}
