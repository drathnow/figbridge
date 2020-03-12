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

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import zedi.fg.tester.util.Constants;

public class TraceWindow extends JFrame
{
    private JMenuBar menuBar = new JMenuBar();
    private TextPaneAppender appender;

    public TraceWindow()
    {
        this(new ConsoleTextPane());
    }

    public TraceWindow(ConsoleTextPane consoleTextPane)
    {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        appender = new TextPaneAppender(consoleTextPane, new PatternLayout("%m%n"));
        Logger.getLogger(Constants.TRACE_LOGGER_NAME).addAppender(appender);

        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 800));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(consoleTextPane);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.add(consoleTextPane);

        buildFileMenu();
        setJMenuBar(menuBar);
        
        addWindowListener(new TraceWindowListener());       

    }

    private JMenu buildFileMenu()
    {
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem exitMenuItem = new JMenuItem("Close");
        exitMenuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                TraceWindow.this.dispose();
            }
        });

        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    class TraceWindowListener extends WindowAdapter {
        public void windowClosed(WindowEvent e)
        {
            Logger.getLogger(Constants.TRACE_LOGGER_NAME).addAppender(appender);
        }
    }
}
