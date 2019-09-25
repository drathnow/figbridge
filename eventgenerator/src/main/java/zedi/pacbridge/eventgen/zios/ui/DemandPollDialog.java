package zedi.pacbridge.eventgen.zios.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.utl.StringUtilities;

public class DemandPollDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField indexTextField;
    private JTextField pollsetTextField;

    private StaticEventGenerator eventGenerator;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            DemandPollDialog dialog = new DemandPollDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject
    public DemandPollDialog(StaticEventGenerator eventGenerator) {
        this();
        this.eventGenerator = eventGenerator;
    }
    
    /**
     * Create the dialog.
     */
    public DemandPollDialog() {
        setBounds(100, 100, 211, 164);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("Index");
        lblNewLabel.setBounds(41, 30, 46, 14);
        lblNewLabel.setHorizontalAlignment(JLabel.RIGHT);
        contentPanel.add(lblNewLabel);
        
        indexTextField = new JTextField();
        indexTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        indexTextField.setText("0");
        indexTextField.setBounds(90, 27, 57, 20);
        contentPanel.add(indexTextField);
        indexTextField.setColumns(10);
        {
            JLabel lblPollsetNumber = new JLabel("Pollset Number");
            lblPollsetNumber.setHorizontalAlignment(SwingConstants.RIGHT);
            lblPollsetNumber.setBounds(16, 64, 71, 14);
            contentPanel.add(lblPollsetNumber);
        }
        {
            pollsetTextField = new JTextField();
            pollsetTextField.setHorizontalAlignment(SwingConstants.RIGHT);
            pollsetTextField.setText("0");
            pollsetTextField.setBounds(90, 61, 57, 20);
            contentPanel.add(pollsetTextField);
            pollsetTextField.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton sendButton = new JButton("Send");
                sendButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (StringUtilities.isNumericString(pollsetTextField.getText()) == false) {
                            JOptionPane.showMessageDialog(DemandPollDialog.this, "Pollset Number must be numeric", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                            pollsetTextField.requestFocus();
                        } else if (StringUtilities.isNumericString(indexTextField.getText()) == false) {
                            JOptionPane.showMessageDialog(DemandPollDialog.this, "Index must be numeric", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                            indexTextField.requestFocus();
                        } else {
                            eventGenerator.publishDemandPoll(Long.parseLong(indexTextField.getText()), Integer.parseInt(pollsetTextField.getText()));
                        }

                    }
                });
                sendButton.setActionCommand("Send");
                buttonPane.add(sendButton);
                getRootPane().setDefaultButton(sendButton);
            }
            {
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                closeButton.setActionCommand("Close");
                buttonPane.add(closeButton);
            }
        }
    }
}
