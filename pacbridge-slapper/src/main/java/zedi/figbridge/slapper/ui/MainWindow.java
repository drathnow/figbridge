package zedi.figbridge.slapper.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figbridge.slapper.BridgeSlapper;
import zedi.swingutl.beans.ConsoleTextPane;
import zedi.swingutl.beans.ScrollingConsolePane;
import zedi.swingutl.beans.utl.SwingUtl;

public class MainWindow extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class.getName());
    
    private BridgeSlapper bridgeSlapper;
    private JMenuItem stopMenuItem;
    private JMenuItem startMenuItem;
    private ScrollingConsolePane consolePane;
    private JMenuItem exitMenuItem;
    private JPanel statsPanel;
    private JLabel lblNewLabel;
    private JLabel sentReportCountLabel;
    private JLabel lblDelinquentReports;
    private JLabel delinquentReportCountLabel;
    private JPanel sentPanel;
    private JPanel delinquentPanel;
    private JButton refreshButton;
    private JMenu mnHelp;
    private JMenuItem mntmAbout;
    private HelpAboutDialog helpAboutDialog;

    public MainWindow() {
        initialize();
    }
    
    @Inject
    public MainWindow(BridgeSlapper bridgeSlapper, WindowCloseListener closeListener) {
        this();
        this.bridgeSlapper = bridgeSlapper;
        this.addWindowListener(closeListener);
    }
    
    public ConsoleTextPane getConsoleTextPane() {
        return consolePane.getConsoleTextPane();
    }
    
    /**
     * Initialize the contents of the 
     */
    private void initialize() {
        setBounds(100, 100, 1300, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.helpAboutDialog = new HelpAboutDialog();
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu controlMenu = new JMenu("Control");
        menuBar.add(controlMenu);
        
        URL iconURL = getClass().getResource("/zedi/figbridge/slapper/zicon.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());
        
        startMenuItem = new JMenuItem("Start");
        startMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    bridgeSlapper.start();
                    startMenuItem.setEnabled(false);
                    stopMenuItem.setEnabled(true);
                } catch (IOException ex) {
                    logger.error("Unable to start", ex);
                }
            }
        });
        
        stopMenuItem = new JMenuItem("Stop");
        stopMenuItem.setEnabled(false);
        stopMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bridgeSlapper.stop();
                startMenuItem.setEnabled(true);
                stopMenuItem.setEnabled(false);
            }
        });

        
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processWindowEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING));
                dispose();
            }
        });
        controlMenu.add(startMenuItem);
        controlMenu.add(stopMenuItem);
        controlMenu.addSeparator();
        controlMenu.add(exitMenuItem);
        
        mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);
        
        mntmAbout = new JMenuItem("About");
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtl.centerWindowInWindow(MainWindow.this, helpAboutDialog);
                helpAboutDialog.setVisible(true);
            }
        });
        mnHelp.add(mntmAbout);
        
        consolePane = new ScrollingConsolePane();
        getContentPane().add(consolePane, BorderLayout.CENTER);
        
        statsPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) statsPanel.getLayout();
        flowLayout.setHgap(10);
        flowLayout.setAlignment(FlowLayout.LEFT);
        getContentPane().add(statsPanel, BorderLayout.SOUTH);
        
        sentPanel = new JPanel();
        sentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        statsPanel.add(sentPanel);
        
        lblNewLabel = new JLabel("Total Reports Sent:");
        sentPanel.add(lblNewLabel);
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        sentReportCountLabel = new JLabel("0");
        sentPanel.add(sentReportCountLabel);
        sentReportCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sentReportCountLabel.setSize(20, 5);
        
        delinquentPanel = new JPanel();
        delinquentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        statsPanel.add(delinquentPanel);
        
        lblDelinquentReports = new JLabel("Delinquent Reports:");
        delinquentPanel.add(lblDelinquentReports);
        
        delinquentReportCountLabel = new JLabel("0");
        delinquentPanel.add(delinquentReportCountLabel);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Long totalReportCount = bridgeSlapper.getDeviceConglomerator().getTotalReportCount();
                Long delquentCount = bridgeSlapper.getDeviceConglomerator().getDelinquentReports();
                delinquentReportCountLabel.setText(delquentCount.toString());
                sentReportCountLabel.setText(totalReportCount.toString());
            }
        });
        statsPanel.add(refreshButton);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
