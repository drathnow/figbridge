package zedi.pacbridge.eventgen.zios.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.inject.Inject;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.eventgen.Main;
import zedi.pacbridge.eventgen.NotificationNames;
import zedi.pacbridge.eventgen.util.Configuration;
import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.utl.NotificationCenter;

public class StaticPublisher {
    private static final Logger logger = LoggerFactory.getLogger(StaticPublisher.class.getName());
    private static ConsoleAppender consoleApender = new ConsoleAppender(new PatternLayout("%m%n"));
    private MainFrame frame;
    
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().addAppender(consoleApender);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StaticPublisher window = new StaticPublisher();
                    org.apache.log4j.Logger.getRootLogger().removeAppender(consoleApender);
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private StaticEventGenerator eventGenerator;
    private ConsoleTextPane theLoggingTextPane;
    private ConsoleTextPane theEventsTextPane;
    private DemandPollDialog demandPollDialog;
    private ConfigureActionDialog fieldSelectionDialog;
    private PasteItDialog pasteItDialog;
    private EventTestDialog eventTestDialog;
    private ScrubDialog scrubDialog;
    private NotificationCenter notificationCenter;
    
    /**
     * Create the application.
     */
    public StaticPublisher() {
        initialize();
    }

    @Inject
    public StaticPublisher(StaticEventGenerator eventGenerator, 
                           DemandPollDialog demandPollDialog,
                           ConfigureActionDialog fieldSelectionDialog,
                           PasteItDialog pasteItDialog,
                           ScrubDialog scrubDialog,
                           NotificationCenter notificationCenter
//                           ,
//                           EventTestDialog eventTestDialog
                           ) {
        this.eventGenerator = eventGenerator;
        this.demandPollDialog = demandPollDialog;
        this.fieldSelectionDialog = fieldSelectionDialog;
        this.pasteItDialog = pasteItDialog;
        this.scrubDialog = scrubDialog;
        this.notificationCenter = notificationCenter;
        initialize();
    }

    public ConsoleTextPane getTheLoggingTextPane() {
        return theLoggingTextPane;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public ConsoleTextPane getTheEventsTextPane() {
        return theEventsTextPane;
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new MainFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(new Dimension(1000, 800));

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        mnFile.add(mntmExit);
        
        JMenu mnEvents = new JMenu("Events");
        menuBar.add(mnEvents);
        
        JMenuItem mntmConfigure = new JMenuItem("Configure Site");
        mntmConfigure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishConfigureSitesEvent();
                } catch (Exception e1) {
                    logger.error("Unable to publish event", e);
                }
            }
        });
        
        JMenuItem configureDeviceMenuItem = new JMenuItem("Configure Device");
        configureDeviceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishConfigureDeviceEventsEvent();
                } catch (Exception ex) {
                    logger.error("Unable to publish event", ex);
                }
            }
        });
        mnEvents.add(configureDeviceMenuItem);
        
        JMenuItem mntmConfigureEvents = new JMenuItem("Configure Events");
        mntmConfigureEvents.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishConfigureEventsEvent();
                } catch (Exception ex) {
                    logger.error("Unable to publish event", ex);
                }
            }
        });
        mnEvents.add(mntmConfigureEvents);
        
        JMenuItem mntmConfigureIoPoints = new JMenuItem("Configure IO Points");
        mntmConfigureIoPoints.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishConfigureIOPointsEvent();
                } catch (Exception ex) {
                    logger.error("Unable to publish event", ex);
                }
}
        });
        mnEvents.add(mntmConfigureIoPoints);
        
        JMenuItem mntmConfigurePorts = new JMenuItem("Configure Ports");
        mntmConfigurePorts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishConfigurePortsEvent();
                } catch (Exception ex) {
                    logger.error("Unable to publish event", ex);
                }
            }
        });
        mnEvents.add(mntmConfigurePorts);
        mnEvents.add(mntmConfigure);
        
        JMenuItem mntmWriteiopoints = new JMenuItem("WriteIOPoints");
        mntmWriteiopoints.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    eventGenerator.publishWriteIOPointsEvent();
                } catch (Exception e1) {
                    logger.error("Unable to publish event", e);
                }
            }
        });
        
        JMenuItem mntmDemandPoll_1 = new JMenuItem("Demand Poll - PID");
        mntmDemandPoll_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                demandPollDialog.setVisible(true);
            }
        });
        
        JMenuItem mntmConfigure_1 = new JMenuItem("Configure...");
        mntmConfigure_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fieldSelectionDialog.setVisible(true);
            }
        });
        mnEvents.add(mntmConfigure_1);
        mnEvents.add(mntmDemandPoll_1);
        
        JMenuItem mntmScrub = new JMenuItem("Scrub...");
        mntmScrub.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrubDialog.setVisible(true);
            }
        });
        mnEvents.add(mntmScrub);
        mnEvents.add(mntmWriteiopoints);
        
        JMenuItem pasteItMenuItem = new JMenuItem("Paste It...");
        pasteItMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pasteItDialog.setVisible(true);
            }
        });
        mnEvents.add(pasteItMenuItem);
        
        JMenu mnTests = new JMenu("Tests");
        menuBar.add(mnTests);
        
        JMenuItem mntmEventTests = new JMenuItem("Event Tests...");
        mnTests.add(mntmEventTests);
        
        JMenu mnLogging = new JMenu("Logging");
        menuBar.add(mnLogging);
        
        JCheckBoxMenuItem jmsPropertiesCheckBox = new JCheckBoxMenuItem("JMS Properties");
        jmsPropertiesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractButton aButton = (AbstractButton)e.getSource();
                if (aButton.getModel().isSelected())
                    System.setProperty(Main.TRACE_JMS_PROPERTIES_PROPERTY_NAME, "true");
                else
                    System.getProperties().remove(Main.TRACE_JMS_PROPERTIES_PROPERTY_NAME);
            }
        });
        
        JMenuItem mntmTurnOff = new JCheckBoxMenuItem("JMS Trace");
        mntmTurnOff.setSelected(true);
        mntmTurnOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mntmTurnOff.isSelected() == false) {
                    notificationCenter.postNotification(NotificationNames.TURN_TRACE_OFF);                    
                    mntmTurnOff.setSelected(false);
                    jmsPropertiesCheckBox.setEnabled(false);
                } else {
                    notificationCenter.postNotification(NotificationNames.TURN_TRACE_ON);
                    mntmTurnOff.setSelected(true);
                    jmsPropertiesCheckBox.setEnabled(true);
                }
            }
        });
        mnLogging.add(mntmTurnOff);
        mnLogging.add(jmsPropertiesCheckBox);
        
        JMenu usernamesMenu = new JMenu("Usernames");
        menuBar.add(usernamesMenu);
        ButtonGroup buttonGroup = new ButtonGroup();
        
        for (Configuration.Username u : Main.getConfiguration().getUsernames()) {
            JRadioButtonMenuItem usernameCheckBox = new JRadioButtonMenuItem(u.getUsername());
            buttonGroup.add(usernameCheckBox);
            usernameCheckBox.setSelected(u.isDefualt());
            usernameCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JRadioButtonMenuItem theItem = (JRadioButtonMenuItem)e.getSource();
                    Main.getConfiguration().setDefaultUsername(theItem.getText());
                    notificationCenter.postNotification(NotificationNames.DEFAULT_USERNAME_CHANGED);
                }
            });
            usernamesMenu.add(usernameCheckBox);
        }
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerSize(10);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.75);        
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        
        JScrollPane scrollPane1 = new JScrollPane();
        splitPane.setLeftComponent(scrollPane1);
        
        ConsoleTextPane loggingTextPane = new ConsoleTextPane();
        scrollPane1.setViewportView(loggingTextPane);
        theLoggingTextPane = loggingTextPane;
        
        JScrollPane scrollPane2 = new JScrollPane();
        splitPane.setRightComponent(scrollPane2);
        
        ConsoleTextPane eventsTextPane = new ConsoleTextPane();
        scrollPane2.setViewportView(eventsTextPane);
        theEventsTextPane = eventsTextPane;
    }

}
