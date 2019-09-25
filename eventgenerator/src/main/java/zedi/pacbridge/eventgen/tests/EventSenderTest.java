package zedi.pacbridge.eventgen.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.eventgen.util.ConfigureDevicesEventGenerator;
import zedi.pacbridge.eventgen.util.ConfigureIoPointsEventGenerator;
import zedi.pacbridge.eventgen.util.ConfigurePortsEventGenerator;
import zedi.pacbridge.eventgen.util.ConfigureSitesEventGenerator;
import zedi.pacbridge.eventgen.util.DemandPollEventGenerator;
import zedi.pacbridge.eventgen.util.WriteIoPointsEventGenerator;
import zedi.pacbridge.utl.SiteAddress;

import com.google.inject.Inject;

public class EventSenderTest {
    private static final Logger logger = LoggerFactory.getLogger(EventSenderTest.class.getName());
    
    public static final Integer DEFAULT_DELAY_SECONDS = 1;
    
    private EventHandler eventPublisher;
    private ConfigureDevicesEventGenerator configureDevicesEventGenerator;
    private ConfigureIoPointsEventGenerator configureIoPointsEventGenerator;
    private ConfigurePortsEventGenerator configurePortsEventGenerator;
    private ConfigureSitesEventGenerator configureSitesEventGenerator;
    private DemandPollEventGenerator demandPollEventGenerator;
    private WriteIoPointsEventGenerator writeIoPointsEventGenerator;
    private SiteAddress siteAddress;
    private Integer numberOfIterations;
    private TestRunner runner;
    private Thread thread;
    
    @Inject
    public EventSenderTest(EventHandler eventPublisher, 
                           ConfigureDevicesEventGenerator configureDevicesEventGenerator, 
                           ConfigureIoPointsEventGenerator configureIoPointsEventGenerator, 
                           ConfigurePortsEventGenerator configurePortsEventGenerator,
                           ConfigureSitesEventGenerator configureSitesEventGenerator, 
                           DemandPollEventGenerator demandPollEventGenerator, 
                           WriteIoPointsEventGenerator writeIoPointsEventGenerator) {
        this.numberOfIterations = 0;
        this.configureDevicesEventGenerator = configureDevicesEventGenerator;
        this.configureIoPointsEventGenerator = configureIoPointsEventGenerator;
        this.configurePortsEventGenerator = configurePortsEventGenerator;
        this.configureSitesEventGenerator = configureSitesEventGenerator;
        this.demandPollEventGenerator = demandPollEventGenerator;
        this.writeIoPointsEventGenerator = writeIoPointsEventGenerator;
    }

    public void setNumberOfIterations(Integer numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }
    
    public void setSiteAddress(SiteAddress siteAddress) {
        this.siteAddress = siteAddress;
    }
    
    public void start() {
        if (siteAddress == null)
            throw new IllegalStateException("SiteAddress is not set");
        if (runner != null) {
            TestPredicate predicate = numberOfIterations > 0 ? new IterationPredicate(numberOfIterations) : new ForEverPredicate();
            runner = new TestRunner(predicate);
            thread = new Thread(runner);
            thread.start();
        }
    }
    
    public void stop() {
        runner.getTestPredicate().stop();
        thread.interrupt();
        try {
            thread.join(2000L);
            thread = null;
            runner = null;
        } catch (InterruptedException e) {
        }
    }

    private void executeTest() throws Exception {
        eventPublisher.publishEvent(configureDevicesEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(configureIoPointsEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(configureIoPointsEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(configurePortsEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(configureSitesEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(demandPollEventGenerator.eventForSiteAddress(siteAddress));
        eventPublisher.publishEvent(writeIoPointsEventGenerator.eventForSiteAddress(siteAddress));
    }

    class TestRunner implements Runnable {
        private TestPredicate testPredicate;
        
        public TestRunner(TestPredicate testPredicate) {
            this.testPredicate = testPredicate;
        }

        public TestPredicate getTestPredicate() {
            return testPredicate;
        }
        
        @Override
        public void run() {
            try {
                while (testPredicate.shouldStopTest() == false) {
                    executeTest();
                    Thread.sleep(DEFAULT_DELAY_SECONDS * 1000L);
                }
            } catch (InterruptedException e) {
            } catch (Exception e) {
                logger.error("Test aborted due to exception", e);
            }
                
        }
    }
    
    interface TestPredicate {
        boolean shouldStopTest();
        void stop();
    }
    
    class ForEverPredicate implements TestPredicate {
        boolean stopped;
        
        public ForEverPredicate() {
            this.stopped = false;
        }
        
        @Override
        public boolean shouldStopTest() {
            return stopped;
        }

        @Override
        public void stop() {
            stopped = true;
        }
        
    }
    
    class IterationPredicate implements TestPredicate {
        private int iterations;
        private boolean stopped;
        
        public IterationPredicate(int iterations) {
            this.iterations = iterations;
            this.stopped = false;
        }

        @Override
        public boolean shouldStopTest() {
            return iterations-- > 0 && stopped == false;
        }

        @Override
        public void stop() {
            stopped = true;
        }
    }
}
