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

public class DeleteEventDialog extends JDialog
{
    private AppController appController;
    private JTextField idField;

    @Inject
    public DeleteEventDialog(AppController appController)
    {
        this();
        this.appController = appController;
    }

    public DeleteEventDialog()
    {
        setBounds(100, 100, 364, 225);
        setTitle("Delete Event");
        setResizable(false);
        getContentPane().setLayout(null);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!StringUtilities.isNumericString(idField.getText()))
                    JOptionPane.showMessageDialog(DeleteEventDialog.this, "You must provide an ID", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else
                {
                    Integer id = Integer.parseInt(idField.getText());
                    appController.deleteEvent(id);
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

        
        JLabel lblNewLabel_3 = new JLabel("Id");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setBounds(59, 46, 66, 25);
        getContentPane().add(lblNewLabel_3);

        idField = new JTextField();
        idField.setBounds(135, 46, 66, 25);
        getContentPane().add(idField);
        idField.setColumns(10);
    }
}
