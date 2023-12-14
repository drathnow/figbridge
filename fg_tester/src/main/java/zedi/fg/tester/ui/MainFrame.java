package zedi.fg.tester.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import zedi.fg.tester.util.AppController;
import zedi.fg.tester.util.SwingUtl;

public class MainFrame extends JFrame implements ActionListener
{
    private AppController appController;
	private JSplitPane splitPane = new JSplitPane();
	private ConsoleTextPane loggingPane = new ConsoleTextPane();
	private ConsoleTextPane tracePane = new ConsoleTextPane();
    private JMenu sendMenu = new JMenu("Send");
    private JMenu testSetupMenu = new JMenu("Test Setup");
    private DemandPollDialog demandPollDialog;
    private ScrubDialog srubDialog;
    private AddEventDialog addEventDialog;
    private DeleteEventDialog deleteEventDialog;
    private PreferencesDialog preferenceDialog;
    private AddSiteDialog addSiteDialog;
    private AddIOPointsDialog addIOPointsDialog;
    private UpdateIOPointDialog updateIOPointsDialog;

	private JMenuBar menuBar = new JMenuBar();

	public MainFrame()
	{
        setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(1000, 800));
		
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setViewportView(loggingPane);
        
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setViewportView(tracePane);

		splitPane.setResizeWeight(0.75);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(scrollPane1);
		splitPane.setRightComponent(scrollPane2);
        splitPane.setDividerSize(10);
		
		getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel statusPanel = new JPanel();
		JTextField textField = new JTextField();
		statusPanel.setLayout(new BorderLayout());
        statusPanel.add(textField,  BorderLayout.CENTER);
        getContentPane().add(new StatusPanel(), BorderLayout.SOUTH);

        buildMenuBar();
		setJMenuBar(menuBar);
        
		addWindowListener(new MainFrameWindowListener());
	}

	public MainFrame(AppController appController)
	{
		this();
		this.appController = appController;
		this.appController.setMainFrame(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.appController.shutdown();
		this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	public ConsoleTextPane getLoggingPane()
	{
		return loggingPane;
	}

	public ConsoleTextPane getTracePane()
	{
		return tracePane;
	}

	public void enableMenus()
	{
        sendMenu.setEnabled(true);
        testSetupMenu.setEnabled(true);
	}
	
	public void disableMenu()
	{
        sendMenu.setEnabled(false);
        testSetupMenu.setEnabled(false);
	}

	private void buildMenuBar()
	{
		menuBar.add(buildFileMenu());
        menuBar.add(buildSendMenu());
        menuBar.add(buildTestSetupMenu());
        menuBar.add(buildViewMenu());
	}

	private JMenu buildTestSetupMenu() 
	{
        JMenuItem modbusSetupMenuItem = new JMenuItem("Modbus Test");
        modbusSetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	appController.setupModbusTest();
            }
        });
        testSetupMenu.add(modbusSetupMenuItem);
		
        JMenuItem reportSystemIOsMenuItem = new JMenuItem("Report System IOs Test");
        reportSystemIOsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                appController.setupReportSystemIOsTest();
            }
        });
        testSetupMenu.add(reportSystemIOsMenuItem);

        JMenuItem aidiSetupMenuItem = new JMenuItem("AIDI Test");
        aidiSetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                appController.setupAIDITest();
            }
        });
        testSetupMenu.add(aidiSetupMenuItem);

        JMenuItem snfSetupMenuItem = new JMenuItem("S&F Test");
        snfSetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                appController.setupSNFTest();
            }
        });
        testSetupMenu.add(snfSetupMenuItem);

        JMenuItem a1000SetupMenuItem = new JMenuItem("A1000 Test");
        a1000SetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                appController.setupA100Test();
            }
        });
        testSetupMenu.add(a1000SetupMenuItem);
        
        testSetupMenu.setEnabled(false);
		return testSetupMenu;
	}
	
	private JMenu buildSendMenu()
	{
        
        JMenuItem demanPollMenuItem = new JMenuItem("Demand Poll...");
        demanPollMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (demandPollDialog == null)
            		demandPollDialog = new DemandPollDialog(appController);
            	demandPollDialog.setLocationRelativeTo(MainFrame.this);
                demandPollDialog.setVisible(true);
            }
        });
        sendMenu.add(demanPollMenuItem);

        JMenuItem scrubMenuItem = new JMenuItem("Scrub...");
        scrubMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (srubDialog == null)
                    srubDialog = new ScrubDialog(appController);
                srubDialog.setLocationRelativeTo(MainFrame.this);
                srubDialog.setVisible(true);
            }
        });
        sendMenu.add(scrubMenuItem);

        JMenuItem addEventMenuItem = new JMenuItem("Add Event...");
        addEventMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (addEventDialog == null)
            	    addEventDialog = new AddEventDialog(appController);
            	addEventDialog.setLocationRelativeTo(MainFrame.this);
            	addEventDialog.setVisible(true);
            }
        });
        sendMenu.add(addEventMenuItem);
        
        JMenuItem deleteEventMenuItem = new JMenuItem("Delete Event...");
        deleteEventMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (deleteEventDialog == null)
                    deleteEventDialog = new DeleteEventDialog(appController);
                deleteEventDialog.setLocationRelativeTo(MainFrame.this);
                deleteEventDialog.setVisible(true);
            }
        });
        sendMenu.add(deleteEventMenuItem);

        JMenuItem addSiteMenuItem = new JMenuItem("Add Site...");
        addSiteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addSiteDialog == null)
                    addSiteDialog = new AddSiteDialog(appController);
                addSiteDialog.setLocationRelativeTo(MainFrame.this);
                addSiteDialog.setVisible(true);
            }
        });
        sendMenu.add(addSiteMenuItem);

        JMenuItem addIOPointMenuItem = new JMenuItem("Add IOPoints...");
        addIOPointMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (addIOPointsDialog == null)
                    addIOPointsDialog = new AddIOPointsDialog(appController);
                addIOPointsDialog.setLocationRelativeTo(MainFrame.this);
                addIOPointsDialog.setVisible(true);
            }
        });
        sendMenu.add(addIOPointMenuItem);

        JMenuItem updateIOPointMenuItem = new JMenuItem("Update IOPoints...");
        updateIOPointMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (updateIOPointsDialog == null)
                    updateIOPointsDialog = new UpdateIOPointDialog(appController);
                updateIOPointsDialog.setLocationRelativeTo(MainFrame.this);
                updateIOPointsDialog.setVisible(true);
            }
        });
        sendMenu.add(updateIOPointMenuItem);
        //sendMenu.setEnabled(false);
        
        return sendMenu;
	}
	
	private JMenu buildFileMenu() 
	{
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

	    JMenuItem prefMenuItem = new JMenuItem("Preferences...");
	    prefMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (preferenceDialog == null)
            		preferenceDialog = new PreferencesDialog();
            	preferenceDialog.setLocationRelativeTo(MainFrame.this);
            	preferenceDialog.setVisible(true);
            }
        });

        fileMenu.add(prefMenuItem);
        
        fileMenu.add(new JSeparator());
        
	    JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.dispose();
            }
        });

        fileMenu.add(exitMenuItem);

        return fileMenu;
	}

	private JMenu buildViewMenu() 
	{
		JMenu viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

	    JMenuItem traceWindowMenuItem = new JMenuItem("Trace Window");
	    traceWindowMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
            	TraceWindow traceWindow = new TraceWindow(tracePane);
            	traceWindow.setVisible(true);
            }
        });

        viewMenu.add(traceWindowMenuItem);

        return viewMenu;
	}
	
	class MainFrameWindowListener extends WindowAdapter {
	    public void windowClosed(WindowEvent e)
	    {
	    	appController.shutdown();
	    }
	}
}
