package zedi.pacbridge.eventgen.zios.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import zedi.pacbridge.eventgen.tests.EventSenderTest;
import zedi.pacbridge.utl.NuidSiteAddress;

public class EventTestDialog extends JDialog {
    private JTextField nuidTextField;
    private EventSenderTest eventSenderTest;
    private JTextField interationsTextField;
    
    //@Inject
    public EventTestDialog(EventSenderTest eventSenderTest) {
        this.eventSenderTest = eventSenderTest;
    }
    
    public EventTestDialog() {
        getContentPane().setLayout(null);
        
        nuidTextField = new JTextField();
        nuidTextField.setBounds(95, 23, 131, 20);
        getContentPane().add(nuidTextField);
        nuidTextField.setColumns(10);
        
        JLabel lblNuid = new JLabel("NUID:");
        lblNuid.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNuid.setBounds(50, 26, 35, 14);
        getContentPane().add(lblNuid);
        
        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (nuidTextField.getText().trim().length() == 0)
                    JOptionPane.showMessageDialog(EventTestDialog.this, "Please enter a NUID", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else {
                    int iterations = 0;
                    if (interationsTextField.getText().trim().length() != 0) {
                        try {
                            iterations = Integer.parseInt(interationsTextField.getText().trim());
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(EventTestDialog.this, "Invalid value for Iterations", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } 
                    eventSenderTest.setNumberOfIterations(iterations);
                    eventSenderTest.setSiteAddress(new NuidSiteAddress(nuidTextField.getText().trim(), 17));
                    eventSenderTest.start();
                }
            }
        });
        btnStart.setBounds(38, 98, 89, 23);
        getContentPane().add(btnStart);
        
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eventSenderTest.stop();
            }
        });
        stopButton.setBounds(137, 98, 89, 23);
        getContentPane().add(stopButton);
        
        JLabel lblIterations = new JLabel("Iterations:");
        lblIterations.setHorizontalAlignment(SwingConstants.RIGHT);
        lblIterations.setBounds(32, 57, 53, 14);
        getContentPane().add(lblIterations);
        
        interationsTextField = new JTextField();
        interationsTextField.setText("0");
        interationsTextField.setBounds(95, 54, 42, 20);
        getContentPane().add(interationsTextField);
        interationsTextField.setColumns(10);
    }
}
