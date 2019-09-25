package zedi.figbridge.slapper;

import java.awt.EventQueue;
import java.io.File;
import java.util.Properties;

import javax.swing.UIManager;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figbridge.slapper.config.Configuration;
import zedi.figbridge.slapper.ui.MainWindow;
import zedi.pacbridge.msg.JmsCenter;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.CommandLineParser;
import zedi.swingutl.beans.TextPaneAppender;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    
    public static final String TITLE = "Bridge Slapper";
    public static final String CONFIG_ARG = "config";
    public static final String NAME_ARG = "name";
    
    private static String name = null;

    public static void main(String[] args) {
        
        Properties arguments = CommandLineParser.argumentsFromCommandLine(args);
        if (arguments.containsKey(CONFIG_ARG)) {
            String configFilename = arguments.getProperty(CONFIG_ARG);
            if (configFilename.isEmpty())
                usage();
            Configuration.setConfigurationFilename(new File(configFilename).getAbsolutePath());
        }
        final File configFile = new File(Configuration.getConfigurationFilename());
            
        if (configFile.exists() == false) {
            logger.error("Configuration file does not exist: " + configFile.getAbsolutePath());
            System.exit(0);
        }
        
        final Appender consoleAppender = org.apache.log4j.Logger.getRootLogger().getAppender("console");
        final Layout layout = consoleAppender.getLayout();
        final Injector injector = Guice.createInjector(new InjectModel());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        String title = TITLE;
                        MainWindow window = injector.getInstance(MainWindow.class);
                        TextPaneAppender appender = new TextPaneAppender(window.getConsoleTextPane(), layout);
                        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
                        org.apache.log4j.Logger.getRootLogger().removeAppender(consoleAppender);
                        window.setVisible(true);

                        logger.info("Using configuration file " + configFile.getAbsolutePath());
                        NetworkEventDispatcherManager manager = injector.getInstance(NetworkEventDispatcherManager.class);
                        EventListener eventListener = injector.getInstance(EventListener.class);
                        Configuration configuration = injector.getInstance(Configuration.class);
                        if (configuration.getName() == Configuration.DEFAULT_NAME)
                            title += " - " + configFile.getAbsolutePath();
                        else 
                            title += " - " + configuration.getName();
                        window.setTitle(title);
                        JmsCenter center = injector.getInstance(JmsCenter.class);
                        center.registerMessageListener(eventListener, configuration.getRawDataTopic(), false);
                        manager.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.out.println("Usage: bridgeslapper [--config=<config-file>]");
        System.exit(0);
    }
}