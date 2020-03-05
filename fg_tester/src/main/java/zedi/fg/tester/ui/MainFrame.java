package zedi.fg.tester.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.fg.tester.util.AppController;

public class MainFrame extends JFrame implements ActionListener
{
    private AppController appController;
	private JSplitPane splitPane = new JSplitPane();
	private ConsoleTextPane loggingPane = new ConsoleTextPane();
	private ConsoleTextPane tracePane = new ConsoleTextPane();
	private DemandPollDialog demandPollDialog;
    private JMenu sendMenu = new JMenu("Send");

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
	
	private void buildMenuBar()
	{
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

	    JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.dispose();
            }
        });

        fileMenu.add(exitMenuItem);
        
        menuBar.add(sendMenu);
        
        JMenuItem demanPollMenuItem = new JMenuItem("Demand Poll");
        demanPollMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (demandPollDialog == null)
            		demandPollDialog = new DemandPollDialog(appController);
                demandPollDialog.setVisible(true);
            }
        });
        sendMenu.add(demanPollMenuItem);
        sendMenu.setEnabled(false);
	}

	public void enableSendMenu()
	{
        sendMenu.setEnabled(true);
	}
	
	public void disableSendMenu()
	{
        sendMenu.setEnabled(false);
	}

	class MainFrameWindowListener extends WindowAdapter {
	    public void windowClosed(WindowEvent e)
	    {
	    	appController.shutdown();
	    }
	}
}
