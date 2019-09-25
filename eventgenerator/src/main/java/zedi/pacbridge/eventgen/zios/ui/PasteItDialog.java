package zedi.pacbridge.eventgen.zios.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import zedi.pacbridge.eventgen.EventPublisher;

import com.google.inject.Inject;

public class PasteItDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextPane theGoods;
    private EventPublisher eventPublisher;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            PasteItDialog dialog = new PasteItDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Inject 
    public PasteItDialog(EventPublisher eventPublisher) {
        this();
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create the dialog.
     */
    public PasteItDialog() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            {
                theGoods = new JTextPane();
                scrollPane.setViewportView(theGoods);
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String eventString = theGoods.getText();
                        if (eventPublisher!= null)
                            eventPublisher.publishStingAsEventString(eventString);
                        else
                            System.out.println("I'd publish if I had an EventPublisher");
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

}
