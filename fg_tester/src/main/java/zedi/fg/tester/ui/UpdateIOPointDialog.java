package zedi.fg.tester.ui;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import zedi.fg.tester.util.AppController;
import zedi.pacbridge.utl.StringUtilities;
import zedi.pacbridge.zap.messages.TimedEventType;
import zedi.pacbridge.zap.values.ZapDataType;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class UpdateIOPointDialog extends JDialog
{
    private static final ZapDataType[] TYPE_OPTIONS = new ZapDataType[] {
                    ZapDataType.Discrete, 
                    ZapDataType.Byte, 
                    ZapDataType.UnsignedByte, 
                    ZapDataType.Integer, 
                    ZapDataType.UnsignedInteger, 
                    ZapDataType.Long, 
                    ZapDataType.UnsignedLong, 
                    ZapDataType.Float, 
                    ZapDataType.Double,
                    ZapDataType.Binary, 
                    ZapDataType.String };
    
    private AppController appController;
    private JTextField tagTextField;
    private JTextField ioPointIdTextField;
    private JTextField siteIdTextField;

    
    public UpdateIOPointDialog(AppController appController)
    {
        setBounds(100, 100, 500, 250);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.appController = appController;
        this.getContentPane().setLayout(null);
        
        JLabel lblIoPointId = new JLabel("IO Point Id:");
        lblIoPointId.setHorizontalAlignment(SwingConstants.RIGHT);
        lblIoPointId.setBounds(53, 45, 98, 15);
        getContentPane().add(lblIoPointId);
        
        JLabel lblTag = new JLabel("Tag:");
        lblTag.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTag.setBounds(81, 72, 70, 15);
        getContentPane().add(lblTag);
        
        JComboBox<ZapDataType> dataTypeComboBox = new JComboBox<>(TYPE_OPTIONS);
        dataTypeComboBox.setBounds(164, 101, 158, 24);
        getContentPane().add(dataTypeComboBox);
        
        JLabel lblDataType = new JLabel("Data Type:");
        lblDataType.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDataType.setBounds(23, 106, 128, 15);
        getContentPane().add(lblDataType);
        
        tagTextField = new JTextField();
        tagTextField.setBounds(164, 70, 158, 19);
        getContentPane().add(tagTextField);
        tagTextField.setColumns(10);
        
        ioPointIdTextField = new JTextField();
        ioPointIdTextField.setBounds(164, 43, 114, 19);
        getContentPane().add(ioPointIdTextField);
        ioPointIdTextField.setColumns(10);
        
        JButton sendButton = new JButton("Send");
        sendButton.setBounds(135, 169, 117, 25);
        getContentPane().add(sendButton);
        sendButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!StringUtilities.isNumericString(ioPointIdTextField.getText()))
                    JOptionPane.showMessageDialog(UpdateIOPointDialog.this, "You must provide a IO Point ID", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else if (tagTextField.getText().length() == 0)
                    JOptionPane.showMessageDialog(UpdateIOPointDialog.this, "You must provide a tag name", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else if (!StringUtilities.isNumericString(siteIdTextField.getText()))
                    JOptionPane.showMessageDialog(UpdateIOPointDialog.this, "You must provide a SiteId name", "NOBB!!!", JOptionPane.ERROR_MESSAGE);
                else
                {
                    long siteId = Long.parseLong(siteIdTextField.getText());
                    long ioId = Long.parseLong(ioPointIdTextField.getText());
                    String tag = tagTextField.getText();
                    ZapDataType dataType = dataTypeComboBox.getItemAt(dataTypeComboBox.getSelectedIndex());
                    appController.updateIOPoint(siteId, ioId, tag, dataType);
                    dispose();
                }
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(277, 169, 117, 25);
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
        getContentPane().add(closeButton);
        
        JLabel lblSiteid = new JLabel("SiteId:");
        lblSiteid.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSiteid.setBounds(81, 14, 70, 15);
        getContentPane().add(lblSiteid);
        
        siteIdTextField = new JTextField();
        siteIdTextField.setBounds(164, 12, 114, 19);
        getContentPane().add(siteIdTextField);
        siteIdTextField.setColumns(10);
    }
}
