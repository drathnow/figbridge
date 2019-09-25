package zedi.figbridge.slapper.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import zedi.figbridge.slapper.Main;
import zedi.pacbridge.utl.VersionNumberExtractor;

public class HelpAboutDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            HelpAboutDialog dialog = new HelpAboutDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public HelpAboutDialog() {
        setModal(true);
        setBounds(100, 100, 398, 217);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
 
        contentPanel.setLayout(null);
        {
            JLabel lblNewLabel = new JLabel();
            lblNewLabel.setBounds(10, 11, 100, 114);
            try {
                InputStream inputStream = getClass().getResourceAsStream("/zedi/figbridge/slapper/spank.png");
                BufferedImage img = ImageIO.read(inputStream);
                Image dimg = img.getScaledInstance(lblNewLabel.getWidth(), lblNewLabel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(dimg);
                lblNewLabel.setIcon(imageIcon);
                setIconImage(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
            contentPanel.add(lblNewLabel);
        }
        {
            JLabel lblBridgeSlapper = new JLabel("Bridge Slapper");
            lblBridgeSlapper.setBounds(209, 31, 80, 14);
            contentPanel.add(lblBridgeSlapper);
        }
        {
            String versionString = VersionNumberExtractor.versionNumberExtractorForClass(Main.class).getVersionNumber();
            JLabel lblVersion = new JLabel(versionString);
            lblVersion.setBounds(220, 56, 58, 14);
            contentPanel.add(lblVersion);
        }
        {
            JLabel lblcSpankmierSolutions = new JLabel("(c) Spankmeir Solutions.  All rights Reservered");
            lblcSpankmierSolutions.setBounds(131, 82, 236, 14);
            contentPanel.add(lblcSpankmierSolutions);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

}
