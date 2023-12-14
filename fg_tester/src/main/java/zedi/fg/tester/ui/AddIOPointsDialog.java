package zedi.fg.tester.ui;

import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import zedi.fg.tester.util.AppController;
import zedi.pacbridge.utl.StringUtilities;

import javax.swing.JTextField;

public class AddIOPointsDialog extends JDialog
{
    private JTextField siteIdTextField;
    private JTextField numberOfPointTextField;
    private AppController appController;

    public AddIOPointsDialog(AppController appController)
    {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.appController = appController;
        
        setBounds(100, 100, 350, 180);
        setTitle("Add IOPoints");
        getContentPane().setLayout(null);
        
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (StringUtilities.isNumericString(siteIdTextField.getText()) == false)
                {
                    JOptionPane.showMessageDialog(AddIOPointsDialog.this, "SiteID must be numeric", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                    siteIdTextField.requestFocus();
                } else if (StringUtilities.isNumericString(numberOfPointTextField.getText()) == false)
                {
                    JOptionPane.showMessageDialog(AddIOPointsDialog.this, "Number of IO Points must be numeric", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                    numberOfPointTextField.requestFocus();
                } else
                {
                    appController.sendIOPointsToSite(Long.parseLong(siteIdTextField.getText()), Integer.parseInt(numberOfPointTextField.getText()));
                }

            }
        });        sendButton.setBounds(53, 110, 117, 25);
        getContentPane().add(sendButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(182, 110, 117, 25);
        getContentPane().add(closeButton);
        
        JLabel lblSiteId = new JLabel("Site ID:");
        lblSiteId.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSiteId.setBounds(100, 42, 70, 15);
        getContentPane().add(lblSiteId);
        
        JLabel lblNumberOfIopoints = new JLabel("Number of IOPoints:");
        lblNumberOfIopoints.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNumberOfIopoints.setBounds(23, 71, 147, 15);
        getContentPane().add(lblNumberOfIopoints);
        
        siteIdTextField = new JTextField();
        siteIdTextField.setBounds(182, 40, 114, 19);
        getContentPane().add(siteIdTextField);
        siteIdTextField.setColumns(10);
        
        numberOfPointTextField = new JTextField();
        numberOfPointTextField.setBounds(182, 71, 114, 19);
        getContentPane().add(numberOfPointTextField);
        numberOfPointTextField.setColumns(10);
    }
}
