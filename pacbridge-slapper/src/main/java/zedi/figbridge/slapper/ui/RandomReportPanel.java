package zedi.figbridge.slapper.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import zedi.figbridge.slapper.config.FigDeviceConfig;
import zedi.figdevice.emulator.config.RandomReportConfig;

public class RandomReportPanel extends JPanel {
    private JTextField intervalTextField;
    private JTextField minReadingsPerReportTextField;
    private JTextField maxReadingsPerReportTextField;
    private JTextField startDelaySecondsTextField;
    private JTextField reconnectMinutesTextField;
    private JTextField textField;
    private ValueTypeComboBox intervalTypeComboBox;

    public RandomReportPanel(FigDeviceConfig deviceConfig, RandomReportConfig reportConfig) {
        this();
        startDelaySecondsTextField.setText(deviceConfig.getStartDelaySeconds().toString());
        if (deviceConfig.getReconnectSeconds() > 0)
            reconnectMinutesTextField.setText(Integer.toString(deviceConfig.getReconnectSeconds() / 60));
        else
            reconnectMinutesTextField.setText("0");
        intervalTextField.setText(reportConfig.getIntervalSeconds().toString());
        minReadingsPerReportTextField.setText(reportConfig.getMinNumberOfReadings().toString());
        maxReadingsPerReportTextField.setText(reportConfig.getMaxNumberOfReadings().toString());
    }

    /**
     * Create the panel.
     */
    public RandomReportPanel() {
        setLayout(null);
        
        JLabel lblNewLabel = new JLabel("Interval:");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel.setBounds(125, 103, 46, 14);
        add(lblNewLabel);
        
        intervalTextField = new JTextField();
        intervalTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        intervalTextField.setBounds(175, 100, 50, 20);
        add(intervalTextField);
        intervalTextField.setColumns(10);
        
        JLabel lblNewLabel_1 = new JLabel("Seconds");
        lblNewLabel_1.setBounds(235, 103, 46, 14);
        add(lblNewLabel_1);
        
        JLabel lblMinReadingsreport = new JLabel("Min. No. Readings/Report:");
        lblMinReadingsreport.setBounds(43, 132, 128, 14);
        add(lblMinReadingsreport);
        
        JLabel lblMaxNoReadingsreport = new JLabel("Max. No. Readings/Report:");
        lblMaxNoReadingsreport.setHorizontalAlignment(SwingConstants.RIGHT);
        lblMaxNoReadingsreport.setBounds(29, 163, 142, 14);
        add(lblMaxNoReadingsreport);
        
        minReadingsPerReportTextField = new JTextField();
        minReadingsPerReportTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        minReadingsPerReportTextField.setBounds(175, 129, 50, 20);
        add(minReadingsPerReportTextField);
        minReadingsPerReportTextField.setColumns(10);
        
        maxReadingsPerReportTextField = new JTextField();
        maxReadingsPerReportTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        maxReadingsPerReportTextField.setBounds(175, 160, 50, 20);
        add(maxReadingsPerReportTextField);
        maxReadingsPerReportTextField.setColumns(10);
        
        JLabel lblStartDelay = new JLabel("Start Delay:");
        lblStartDelay.setHorizontalAlignment(SwingConstants.RIGHT);
        lblStartDelay.setBounds(155, 47, 70, 14);
        add(lblStartDelay);
        
        startDelaySecondsTextField = new JTextField();
        startDelaySecondsTextField.setText("0");
        startDelaySecondsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        startDelaySecondsTextField.setBounds(230, 44, 50, 20);
        add(startDelaySecondsTextField);
        startDelaySecondsTextField.setColumns(10);
        
        JLabel lblSeconds = new JLabel("Seconds");
        lblSeconds.setBounds(291, 47, 46, 14);
        add(lblSeconds);
        
        JLabel lblReconnect = new JLabel("Reconnect:");
        lblReconnect.setHorizontalAlignment(SwingConstants.RIGHT);
        lblReconnect.setBounds(170, 14, 55, 14);
        add(lblReconnect);
        
        reconnectMinutesTextField = new JTextField();
        reconnectMinutesTextField.setText("0");
        reconnectMinutesTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        reconnectMinutesTextField.setBounds(230, 11, 51, 20);
        add(reconnectMinutesTextField);
        reconnectMinutesTextField.setColumns(10);
        
        JLabel lblMinutes = new JLabel("Minutes");
        lblMinutes.setBounds(291, 14, 46, 14);
        add(lblMinutes);
        
        JLabel label = new JLabel("IntervalType:");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setBounds(10, 14, 68, 14);
        add(label);
        
        intervalTypeComboBox = new ValueTypeComboBox();
        intervalTypeComboBox.setBounds(88, 11, 68, 20);
        add(intervalTypeComboBox);
        
        textField = new JTextField();
        textField.setColumns(10);
        textField.setBounds(88, 44, 55, 20);
        add(textField);
        
        JLabel label_1 = new JLabel("Count:");
        label_1.setHorizontalAlignment(SwingConstants.RIGHT);
        label_1.setBounds(45, 47, 33, 14);
        add(label_1);
    }
}
