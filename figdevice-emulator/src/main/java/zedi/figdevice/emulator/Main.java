package zedi.figdevice.emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import zedi.figdevice.emulator.config.Configuration;
import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.figdevice.emulator.utl.FixedIntervalReportTimeGenerator;
import zedi.figdevice.emulator.utl.FixedReportGenerator;
import zedi.figdevice.emulator.utl.RandomValueGenerator;
import zedi.figdevice.emulator.utl.ReportIdGenerator;
import zedi.figdevice.emulator.utl.SingleFixedBundledReportMessageGenerator;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.CommandLineParser;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NotificationCenter;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    private static final Integer DEFAULT_DELAY_SECONDS = 30;
    private static final String DEFAULT_CONFIG_FILE = "figdevice.xml";
    private static final String DEFAULT_DEVICE_NAME = "Slapper1";
    private static final String CONFIG_OPTION_NAME = "config";
    private static final String SINGLE_OPTION_NAME = "single";
    private static final String DELAY_OPTION_NAME = "delaySeconds";
    private static final String NUMBER_OF_REPORTS = "numberOfReports";
    private static final String NUMBER_OF_ITEMS = "numberOfItems";
    
    public static void main(String[] args) {
        String configFileName = DEFAULT_CONFIG_FILE;
        BundledReportMessageGenerator reportGenerator = null;
        Integer delaySeconds = DEFAULT_DELAY_SECONDS;
        
        Properties arguments = CommandLineParser.argumentsFromCommandLine(args);
        if (arguments.containsKey(CONFIG_OPTION_NAME))
            configFileName = arguments.getProperty(CONFIG_OPTION_NAME);

        if (arguments.containsKey(SINGLE_OPTION_NAME)) {
            if (arguments.containsKey(NUMBER_OF_ITEMS) == false || arguments.containsKey(NUMBER_OF_REPORTS) == false)
                usage();
            Integer numberOfReports = new Integer(arguments.getProperty(NUMBER_OF_REPORTS));
            Integer numberOfItems = new Integer(arguments.getProperty(NUMBER_OF_ITEMS));
            RandomValueGenerator readingGenerator = new RandomValueGenerator();
            FixedReportGenerator fixedReportGenerator = new FixedReportGenerator(numberOfItems, readingGenerator, new ReportIdGenerator(), new FixedIntervalReportTimeGenerator(0));
            reportGenerator = new SingleFixedBundledReportMessageGenerator(numberOfReports, fixedReportGenerator);
            delaySeconds = new Integer(arguments.getProperty(DELAY_OPTION_NAME, DEFAULT_DELAY_SECONDS.toString()));
                
        }
        
        Injector injector = Guice.createInjector(new InjectModel());
        final NetworkEventDispatcherManager manager = injector.getInstance(NetworkEventDispatcherManager.class);
        manager.start();
        NotificationCenter notificationCenter = injector.getInstance(NotificationCenter.class);
        GlobalScheduledExecutor globalScheduledExecutor = injector.getInstance(GlobalScheduledExecutor.class);
            
        try {
            Configuration configuration = loadConfiguration(configFileName);
            if (reportGenerator == null)
                reportGenerator = configuration.reportGenerator();
            InetSocketAddress bridgeAddress = configuration.getBridgeAddress();
            final FigDevice device = new FigDevice(DEFAULT_DEVICE_NAME, notificationCenter, bridgeAddress, reportGenerator, delaySeconds);
            globalScheduledExecutor.schedule(new Runnable(){
                @Override
                public void run() {
                    device.start(manager);
                }
            }, 1);
            
            while (device.isStopped() == false)
                Thread.sleep(1000);
            manager.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Configuration loadConfiguration(String configFileName) throws IOException, JDOMException {
        InputStream inputStream = new FileInputStream(new File(configFileName));
        Element configElement = JDomUtilities.elementForInputStream(inputStream);
        return Configuration.configurationFromElement(configElement);
    }
    
    
    private static void usage() {
        System.out.println("Usage: figdevice-emulator [--config=<config-file|figdevice.xml>] [--delaySeconds=<seconds|30>] [--single --numberOfReports=<count> --numberOfItems=<count>]");
        System.exit(1);
    }
}
