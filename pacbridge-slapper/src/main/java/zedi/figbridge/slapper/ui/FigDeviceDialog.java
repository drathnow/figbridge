package zedi.figbridge.slapper.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import zedi.figbridge.slapper.config.FigDeviceConfig;
import zedi.figdevice.emulator.config.FixedReportConfig;
import zedi.figdevice.emulator.config.RandomReportConfig;
import zedi.figdevice.emulator.config.ReportConfig;
import zedi.figdevice.emulator.utl.ValueType;

public class FigDeviceDialog extends JDialog {
    private JTextField countTextField;
    private ValueTypeComboBox intervalTypeComboBox;
    private ValueTypeComboBox reportTypeComboBox;
    private JPanel contentPanel;
    
    public FigDeviceDialog(FigDeviceConfig deviceConfig) {
        JPanel thePanel;
        ReportConfig reportConfig = deviceConfig.getReportConfig();
        if (reportConfig instanceof FixedReportConfig) {
            reportTypeComboBox.setSelectedItem(ValueType.FIXED);
            countTextField.setText(deviceConfig.getDeviceCount().toString());
            thePanel = new FixedReportPanel(deviceConfig, (FixedReportConfig)reportConfig);
        } else {
            reportTypeComboBox.setSelectedItem(ValueType.RANDOM);
            countTextField.setText(deviceConfig.getDeviceCount().toString());
            thePanel = new RandomReportPanel(deviceConfig, (RandomReportConfig)reportConfig);
        }
        contentPanel.add(thePanel, BorderLayout.CENTER);
    }
    
    /**
     * Create the panel.
     */
    public FigDeviceDialog() {
        setResizable(false);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        FlowLayout fl_panel = new FlowLayout();
        fl_panel.setAlignment(FlowLayout.LEFT);
        panel.setLayout(fl_panel);
        
        JLabel lblReportType = new JLabel("Report Type:");
        lblReportType.setBounds(-32, 5, 64, 14);
        panel.add(lblReportType);
        
        reportTypeComboBox = new ValueTypeComboBox();
        reportTypeComboBox.setBounds(-39, 24, 78, 20);
        reportTypeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ValueTypeComboBox cb = ((ValueTypeComboBox)e.getSource());
                contentPanel.removeAll();
                if (cb != null) {
                    if (cb.getSelectedItem().equals(ValueType.RANDOM))
                        contentPanel.add(new RandomReportPanel(), BorderLayout.CENTER);
                    else 
                        contentPanel.add(new FixedReportPanel(), BorderLayout.CENTER);
                    FigDeviceDialog.this.validate();
                }
            }
        });
        panel.add(reportTypeComboBox);
        
        JLabel lblNewLabel = new JLabel("Count:");
        lblNewLabel.setBounds(-16, 49, 33, 14);
        panel.add(lblNewLabel);
        
        countTextField = new JTextField();
        countTextField.setBounds(-43, 68, 86, 20);
        panel.add(countTextField);
        countTextField.setColumns(10);
        
        JLabel lblIntervaltype = new JLabel("Interval Type:");
        lblIntervaltype.setBounds(-33, 93, 66, 14);
        panel.add(lblIntervaltype);
        
        intervalTypeComboBox = new ValueTypeComboBox();
        intervalTypeComboBox.setBounds(-39, 112, 78, 20);
        panel.add(intervalTypeComboBox);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        getContentPane().setSize(new Dimension(400, 200));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancelButton);
        
        setSize(new Dimension(500, 300));
    }

    public static void main(String[] args) {
        initLookAndFeel();
        FigDeviceDialog dialog = new FigDeviceDialog();
        dialog.setVisible(true);
    }
    
    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
