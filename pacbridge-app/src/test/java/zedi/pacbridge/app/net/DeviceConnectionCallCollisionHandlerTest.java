package zedi.pacbridge.app.net;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class DeviceConnectionCallCollisionHandlerTest extends BaseTestCase {

	@Test
	public void shouldHandleCallCollisionByClearingSiteAddress() throws Exception {
		DeviceConnection newConnection = mock(DeviceConnection.class);
		DeviceConnection existingConnection = mock(DeviceConnection.class);
		
		DeviceConnectionCallCollisionHandler handler = new DeviceConnectionCallCollisionHandler();
		handler.handleCallCollision(newConnection, existingConnection);
		
		verify(existingConnection).setSiteAddress(DeviceConnectionCallCollisionHandler.COLLIDED_ADDRESS);
		verify(existingConnection).close();
	}
}
