package zedi.pacbridge.app.devices;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.sql.Timestamp;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class DeviceObjectCreatorTest extends BaseTestCase {
	
	private static final String SERIAL_NUMBER = "Foo";
	private static final byte[] BYTES = new byte[]{1, 2};
    private static final byte[] KEY = new byte[]{3, 4};
	private static final Integer NETWORK_NUMBER = 20;
	private static Long NOW = System.currentTimeMillis();
	private static final Timestamp UPDATE_TIME = new Timestamp(NOW); 
	
	@Test
	public void shouldCreateDeviceWithValues() throws Exception {
		KeyDecoder decoder = mock(KeyDecoder.class);
		DeviceObjectCreator creator = new DeviceObjectCreator(decoder);
		
		given(decoder.decodedBytesForBase64EncodedBytes(BYTES)).willReturn(KEY);
		
		Device device = creator.objectForStuff(SERIAL_NUMBER, BYTES, NETWORK_NUMBER, UPDATE_TIME);

		assertEquals(SERIAL_NUMBER, device.getNuid());
		assertArrayEquals(KEY, device.getSecretKey());
		assertEquals(NETWORK_NUMBER, device.getNetworkNumber());
		assertEquals(NOW.longValue(), device.getLastUpdateTime().getTime());
	}

    @Test
    public void shouldCreateDeviceWithDefaultNetworkValueIfNetworkNumberIsZero() throws Exception {
        KeyDecoder decoder = mock(KeyDecoder.class);
        DeviceObjectCreator creator = new DeviceObjectCreator(decoder);
        
        given(decoder.decodedBytesForBase64EncodedBytes(BYTES)).willReturn(KEY);
        
        Device device = creator.objectForStuff(SERIAL_NUMBER, BYTES, 0, UPDATE_TIME);

        assertEquals(SERIAL_NUMBER, device.getNuid());
        assertArrayEquals(KEY, device.getSecretKey());
        assertEquals(DeviceObjectCreator.DEFAULT_NETWORK_NUMBER, device.getNetworkNumber());
        assertEquals(NOW.longValue(), device.getLastUpdateTime().getTime());
    }
}
