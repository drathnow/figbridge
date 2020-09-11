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
import zedi.fg.tester.util.AppController.EventType;
import zedi.pacbridge.utl.StringUtilities;
import zedi.pacbridge.zap.messages.TimedEventType;

import javax.swing.JComboBox;

public class AddEventDialog extends JDialog
{
    private static final String[] TYPE_OPTIONS = new String[] {"Poll", "Report"};

    private AppController appController;
    private JTextField startimeField;
    private JTextField intervalField;
    private JTextField durationField;
    private JTextField pollsetField;
    private JTextField nameField;
    private JComboBox eventTypeComboBox;
    
    @Inject
    public AddEventDialog(AppController appController)
    {
        this();
        this.appController = appController;
    }

    public AddEventDialog()
    {
        setBounds(100, 100, 346, 421);
        setTitle("Add Event");
        setResizable(false);
        getContentPane().setLayout(null);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!StringUtilities.isNumericString(pollsetField.getText()))
                    JOptionPane.showMessageDialog(AddEventDialog.this, "You must provide a PollSet ID", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else if (!StringUtilities.isNumericString(pollsetField.getText()))
                    JOptionPane.showMessageDialog(AddEventDialog.this, "You must provide an Interval", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else if (nameField.getText().length() == 0)
                    JOptionPane.showMessageDialog(AddEventDialog.this, "You must provide an Name", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else
                {
                    String name = nameField.getText();
                    TimedEventType eventType = TimedEventType.Poll;
                    
                    if (eventTypeComboBox.getSelectedItem().toString().equals("Report"))
                        eventType = TimedEventType.Report;
                    
                    Integer startTime = (int)(System.currentTimeMillis() / 1000);
                    Integer duration = 0;
                    Integer pollsetId = Integer.parseInt(pollsetField.getText());
                    Integer interval = Integer.parseInt(intervalField.getText());
                    appController.addEvent(name, eventType, startTime, interval, duration, pollsetId);
                    dispose();
                }
            }
        });

        okButton.setBounds(38, 342, 114, 25);
        getContentPane().add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        cancelButton.setBounds(164, 342, 114, 25);
        getContentPane().add(cancelButton);

        
        JLabel lblNewLabel_3 = new JLabel("Name");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setBounds(59, 46, 66, 25);
        getContentPane().add(lblNewLabel_3);

        JLabel lblType = new JLabel("Type");
        lblType.setHorizontalAlignment(SwingConstants.RIGHT);
        lblType.setBounds(59, 86, 66, 25);
        getContentPane().add(lblType);

        JLabel lblStartTime = new JLabel("Start Time");
        lblStartTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblStartTime.setBounds(38, 123, 87, 25);
        getContentPane().add(lblStartTime);

        JLabel lblNewLabel = new JLabel("Interval");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(59, 163, 66, 25);
        getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Duration");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_1.setBounds(59, 203, 66, 25);
        getContentPane().add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Pollset");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_2.setBounds(59, 243, 66, 25);
        getContentPane().add(lblNewLabel_2);

        nameField = new JTextField();
        nameField.setBounds(135, 46, 174, 25);
        getContentPane().add(nameField);
        nameField.setColumns(10);

        eventTypeComboBox = new JComboBox(TYPE_OPTIONS);
        eventTypeComboBox.setBounds(135, 83, 124, 24);
        getContentPane().add(eventTypeComboBox);

        startimeField = new JTextField();
        startimeField.setText("NOW");
        startimeField.setEnabled(false);
        startimeField.setBounds(135, 123, 124, 25);
        getContentPane().add(startimeField);
        startimeField.setColumns(10);

        intervalField = new JTextField();
        intervalField.setBounds(135, 163, 124, 25);
        getContentPane().add(intervalField);
        intervalField.setColumns(10);

        durationField = new JTextField();
        durationField.setEnabled(false);
        durationField.setBounds(135, 203, 124, 25);
        getContentPane().add(durationField);
        durationField.setColumns(10);

        pollsetField = new JTextField();
        pollsetField.setBounds(135, 243, 124, 25);
        getContentPane().add(pollsetField);
        pollsetField.setColumns(10);        
    }
}
