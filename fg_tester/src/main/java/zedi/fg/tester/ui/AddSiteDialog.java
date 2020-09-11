package zedi.fg.tester.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.google.inject.Inject;

import zedi.fg.tester.util.AppController;
import zedi.pacbridge.utl.StringUtilities;

public class AddSiteDialog extends JDialog
{
    private AppController appController;
    private JTextField nameField;

    @Inject
    public AddSiteDialog(AppController appController)
    {
        this();
        this.appController = appController;
    }

    public AddSiteDialog()
    {
        setBounds(100, 100, 364, 225);
        setTitle("Add Site");
        setResizable(false);
        getContentPane().setLayout(null);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!StringUtilities.isValidInputString(nameField.getText()))
                    JOptionPane.showMessageDialog(AddSiteDialog.this, "You must provide a name", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else
                {
                    appController.addSite(nameField.getText());
                    dispose();
                }
            }
        });

        okButton.setBounds(69, 108, 114, 25);
        getContentPane().add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        cancelButton.setBounds(195, 108, 114, 25);
        getContentPane().add(cancelButton);

        JLabel lblNewLabel_3 = new JLabel("Name");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setBounds(69, 46, 54, 25);
        getContentPane().add(lblNewLabel_3);

        nameField = new JTextField();
        nameField.setBounds(133, 46, 161, 25);
        getContentPane().add(nameField);
        nameField.setColumns(10);
    }
}
