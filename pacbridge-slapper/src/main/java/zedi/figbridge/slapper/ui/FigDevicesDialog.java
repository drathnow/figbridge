package zedi.figbridge.slapper.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import zedi.figbridge.slapper.config.Configuration;
import zedi.figbridge.slapper.config.FigDeviceConfig;
import zedi.figdevice.emulator.config.FixedReportConfig;
import zedi.figdevice.emulator.config.RandomReportConfig;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.swingutl.beans.utl.SwingUtl;

public class FigDevicesDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JPanel editorPanel; 
    private FigDevicesConfigListModel listModel;
    private JList<FigDeviceConfig> deviceConfigList;
    private class FigDeviceConfigCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof FigDeviceConfig) {
                setText(((FigDeviceConfig)value).getDisplayName());
            }
            return this;
        }
    }     
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SwingUtl.initLookAndFeel();
            InputStream inputStream = new FileInputStream(new File("bridgeslapper.xml"));
            Configuration configuration = Configuration.configurationFromElement(JDomUtilities.elementForInputStream(inputStream));
            FigDevicesDialog dialog = new FigDevicesDialog(configuration.getDeviceConfigs());
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FigDevicesDialog(List<FigDeviceConfig> configs) {
        this();
        listModel = new FigDevicesConfigListModel(configs);
        deviceConfigList.setModel(listModel);
        deviceConfigList.setCellRenderer(new FigDeviceConfigCellRenderer());
        deviceConfigList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (deviceConfigList.getSelectedIndex() >= 0) {
                        editorPanel.removeAll();
                        FigDeviceConfig config = listModel.getElementAt(deviceConfigList.getSelectedIndex());
                        if (config.getReportConfig() instanceof FixedReportConfig) {
                            editorPanel.add(new FixedReportPanel(config, (FixedReportConfig)config.getReportConfig()), BorderLayout.CENTER);
                        } else {
                            editorPanel.add(new RandomReportPanel(config, (RandomReportConfig)config.getReportConfig()), BorderLayout.CENTER);                            
                        }
                        FigDevicesDialog.this.validate();
                    }
                }
            }
        });
    }
       
    /**
     * Create the dialog.
     */
    public FigDevicesDialog() {
        setAutoRequestFocus(false);
        setBounds(100, 100, 583, 363);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            deviceConfigList = new JList<>();
            deviceConfigList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            deviceConfigList.setBounds(10, 37, 136, 177);
            contentPanel.add(deviceConfigList);
        }
        {
            editorPanel = new JPanel();
            editorPanel.setBounds(172, 37, 385, 206);
            contentPanel.add(editorPanel);
            editorPanel.setLayout(new BorderLayout(0, 0));
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
}
