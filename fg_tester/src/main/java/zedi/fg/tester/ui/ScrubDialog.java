package zedi.fg.tester.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

import zedi.fg.tester.util.AppController;

public class ScrubDialog extends JDialog {
    
    private AppController appController;
    private JCheckBox ioPointsCheckBox;
    private JCheckBox eventsCheckBox;
    private JCheckBox reportCheckBox;
    private JCheckBox allCheckBox;
    
    @Inject
    public ScrubDialog(AppController appController) {
        this();
        this.appController = appController;
    }
    
    public ScrubDialog() {
        setResizable(false);
        setBounds(100, 100, 275, 250);
        getContentPane().setLayout(null);
        
        ioPointsCheckBox = new JCheckBox("IO Points");
        ioPointsCheckBox.setBounds(21, 56, 97, 23);
        getContentPane().add(ioPointsCheckBox);
        
        eventsCheckBox = new JCheckBox("Events");
        eventsCheckBox.setBounds(21, 84, 97, 23);
        getContentPane().add(eventsCheckBox);
        
        reportCheckBox = new JCheckBox("Reports");
        reportCheckBox.setBounds(21, 110, 97, 23);
        getContentPane().add(reportCheckBox);
        
        allCheckBox = new JCheckBox("All");
        allCheckBox.setBounds(21, 30, 97, 23);
        getContentPane().add(allCheckBox);
        
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	appController.sendScrub(ioPointsCheckBox.isSelected(), eventsCheckBox.isSelected(), reportCheckBox.isSelected(), ioPointsCheckBox.isSelected(), allCheckBox.isSelected());
            }
        });
        sendButton.setBounds(21, 155, 89, 23);
        getContentPane().add(sendButton);
        
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        cancelButton.setBounds(133, 155, 89, 23);
        getContentPane().add(cancelButton);
    }
}