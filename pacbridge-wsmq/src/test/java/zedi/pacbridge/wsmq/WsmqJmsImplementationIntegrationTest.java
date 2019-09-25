package zedi.pacbridge.wsmq;

import javax.jms.JMSException;

import org.junit.Ignore;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class WsmqJmsImplementationIntegrationTest extends BaseTestCase {

    @Test
    @Ignore
    public void shouldConnectAndDisconnect() throws Exception {
        WsmqJmsImplementator implementation = new WsmqJmsImplementator();
        implementation.setQueueManagerName("QM_csmqdev1");
        implementation.setHostName("csmqdev1");
        implementation.setClientId("lclpbridge_id");
        try {
            implementation.initialize();
        } catch (JMSException e) {
            e.getLinkedException().printStackTrace();
        }
        implementation.createConnection();
    }

}
