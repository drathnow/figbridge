package zedi.figbridge.slapper.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import zedi.figbridge.slapper.config.FigDeviceConfig;
import zedi.figdevice.emulator.config.FixedReportConfig;

public class FixedReportPanel extends JPanel {
    private JTextField intervalTextField;
    private JTextField readingPerReportTextField;
    private JTextField startDelaySecondsTextField;
    private JTextField reconnectMinutesTextField;
    private JTextField textField;

    public FixedReportPanel(FigDeviceConfig deviceConfig, FixedReportConfig reportConfig) {
        this();
        startDelaySecondsTextField.setText(deviceConfig.getStartDelaySeconds().toString());
        if (deviceConfig.getReconnectSeconds() > 0)
            reconnectMinutesTextField.setText(Integer.toString(deviceConfig.getReconnectSeconds() / 60));
        else
            reconnectMinutesTextField.setText("0");
        intervalTextField.setText(reportConfig.getIntervalSeconds().toString());
        readingPerReportTextField.setText(reportConfig.getNumberOfReadings().toString());
    }

    /**
     * Create the panel.
     */
    public FixedReportPanel() {
        setLayout(null);
        
        JLabel lblInterval = new JLabel("Interval:");
        lblInterval.setHorizontalAlignment(SwingConstants.RIGHT);
        lblInterval.setBounds(125, 103, 46, 14);
        add(lblInterval);
        
        JLabel lblSeconds = new JLabel("Seconds");
        lblSeconds.setBounds(235, 103, 46, 14);
        add(lblSeconds);
        
        intervalTextField = new JTextField();
        intervalTextField.setBounds(175, 100, 50, 20);
        add(intervalTextField);
        intervalTextField.setColumns(10);
        
        JLabel lblNumberofreadings = new JLabel("No. Readings/Report:");
        lblNumberofreadings.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNumberofreadings.setBounds(54, 132, 117, 14);
        add(lblNumberofreadings);
        
        readingPerReportTextField = new JTextField();
        readingPerReportTextField.setBounds(175, 129, 50, 20);
        add(readingPerReportTextField);
        readingPerReportTextField.setColumns(10);
        
        JLabel label = new JLabel("Reconnect:");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(170, 14, 55, 14);
        add(label);
        
        JLabel label_1 = new JLabel("Start Delay:");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setBounds(167, 47, 58, 14);
        add(label_1);
        
        startDelaySecondsTextField = new JTextField();
        startDelaySecondsTextField.setText("0");
        startDelaySecondsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        startDelaySecondsTextField.setColumns(10);
        startDelaySecondsTextField.setBounds(230, 44, 50, 20);
        add(startDelaySecondsTextField);
        
        JLabel label_2 = new JLabel("Seconds");
        label_2.setBounds(291, 47, 46, 14);
        add(label_2);
        
        JLabel label_3 = new JLabel("Minutes");
        label_3.setBounds(291, 14, 46, 14);
        add(label_3);
        
        reconnectMinutesTextField = new JTextField();
        reconnectMinutesTextField.setText("0");
        reconnectMinutesTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        reconnectMinutesTextField.setColumns(10);
        reconnectMinutesTextField.setBounds(230, 11, 51, 20);
        add(reconnectMinutesTextField);
        
        JLabel lblCount = new JLabel("Count:");
        lblCount.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCount.setBounds(45, 47, 33, 14);
        add(lblCount);
        
        textField = new JTextField();
        textField.setBounds(88, 44, 55, 20);
        add(textField);
        textField.setColumns(10);
        
        JLabel lblIntervaltype = new JLabel("IntervalType:");
        lblIntervaltype.setHorizontalAlignment(SwingConstants.RIGHT);
        lblIntervaltype.setBounds(10, 14, 68, 14);
        add(lblIntervaltype);
        
        ValueTypeComboBox comboBox = new ValueTypeComboBox();
        comboBox.setBounds(88, 11, 68, 20);
        add(comboBox);
    }
}
