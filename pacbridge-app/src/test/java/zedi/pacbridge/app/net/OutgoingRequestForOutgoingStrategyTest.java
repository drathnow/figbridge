package zedi.pacbridge.app.net;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestManager;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;

public class OutgoingRequestForOutgoingStrategyTest extends BaseTestCase {

    @Mock
    private OutgoingRequest outgoingRequest;
    @Mock
    private OutgoingRequestManager outgoingRequestManager;
    @Mock
    private DependencyResolver dependencyResolver;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
        given(dependencyResolver.getImplementationOf(OutgoingRequestManager.JNDI_NAME)).willReturn(outgoingRequestManager);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }
    
    @Test
    public void shouldPassOutgoingRequestToManager() throws Exception {
        OutgoingRequestForOutgoingStrategy strategy = new OutgoingRequestForOutgoingStrategy();
        strategy.handleOutgoingRequest(outgoingRequest);

        verify(dependencyResolver).getImplementationOf(OutgoingRequestManager.JNDI_NAME);
        verify(outgoingRequestManager).queueOutgoingRequest(outgoingRequest);
    }
}
