package zedi.pacbridge.eventgenerator;

import com.google.inject.Guice;
import com.google.inject.Injector;

import zedi.pacbridge.eventgen.InjectModel;
import zedi.pacbridge.eventgen.Main;
import zedi.pacbridge.msg.JmsCenter;
import zedi.pacbridge.test.BaseTestCase;

public abstract class JmsPublisherBaseTest extends BaseTestCase {
    
    static {
        try {
            Main.loadConfig(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static final String EVENTS_TOPIC = "topic://scada/system/events";
    private static final Injector injector = Guice.createInjector(new InjectModel());

    protected ResponseListener listener;
    protected JmsCenter jmsCenter;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        jmsCenter = injector().getInstance(JmsCenter.class);
        jmsCenter.registerMessageListener(listener, EVENTS_TOPIC, false);
        listener = new ResponseListener();
    }
    
    protected static Injector injector() {
        return injector;
    }
    

}
