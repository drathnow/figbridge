package zedi.fg.tester.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class StatusPanel extends JPanel
{
    private JTextField statusField;

    public StatusPanel() 
    {
        statusField = new JTextField();
        setLayout(new BorderLayout());
        add(statusField,  BorderLayout.CENTER);
    }
}
