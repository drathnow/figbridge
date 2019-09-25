package zedi.pacbridge.domain.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

@SuppressWarnings("unchecked")
public class DeviceRepositoryTest extends BaseTestCase {
    private static final Date INIT_DATE = new Date(0);
    private static final byte[] BYTES1 = new byte[]{0x01, 0x02};
    private static final String SERIAL_NUMBER1 = "1234";
    private static final BigDecimal NETWORK_NUMBER1 = new BigDecimal(12);
    private static final java.sql.Timestamp UPDATE_TIME1 = new java.sql.Timestamp(100L);
    
    private static final byte[] BYTES2 = new byte[]{0x05, 0x06};
    private static final String SERIAL_NUMBER2 = "5678";
    private static final BigDecimal NETWORK_NUMBER2 = new BigDecimal(17);
    private static final java.sql.Timestamp UPDATE_TIME2 = new java.sql.Timestamp(200L);

    class TestDevice {
    }

    
    @Test
    public void shouldReturnNullForSingleDeviceWithSerialNumberIfNoneFound() throws Exception {
        EntityManager entityManager = mock(EntityManager.class);
        ObjectCreator<TestDevice> objectCreator = mock(ObjectCreator.class);
        Query query = mock(Query.class);
        
        given(entityManager.createNativeQuery(DeviceRepository.SQL_QUERY_SINGLE)).willReturn(query);
        given(query.setParameter(1, SERIAL_NUMBER1)).willReturn(query);
        given(query.getSingleResult()).willReturn(null);
        
        DeviceRepository repository = new DeviceRepository(entityManager);
        TestDevice device = repository.objectWithSerialNumber(SERIAL_NUMBER1, objectCreator);
        
        assertNull(device);
        verify(entityManager).createNativeQuery(DeviceRepository.SQL_QUERY_SINGLE);
        verify(query).setParameter(DeviceRepository.SERIAL_NUMBER_PARAM_NAME, SERIAL_NUMBER1);
        verify(query).getSingleResult();
    }

    
    @Test
    public void shouldReturnResultsForSingleDeviceWithSerialNumber() throws Exception {
        TestDevice testDevice = new TestDevice();
        EntityManager entityManager = mock(EntityManager.class);
        ObjectCreator<TestDevice> objectCreator = mock(ObjectCreator.class);
        Query query = mock(Query.class);
        Object[] objArray1 = new Object[]{SERIAL_NUMBER1, BYTES1, UPDATE_TIME1, NETWORK_NUMBER1};
        
        given(entityManager.createNativeQuery(DeviceRepository.SQL_QUERY_SINGLE)).willReturn(query);
        given(query.setParameter(1, SERIAL_NUMBER1)).willReturn(query);
        given(query.getSingleResult()).willReturn(objArray1);
        given(objectCreator.objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1)).willReturn(testDevice);
        
        DeviceRepository repository = new DeviceRepository(entityManager);
        TestDevice device = repository.objectWithSerialNumber(SERIAL_NUMBER1, objectCreator);
        
        assertNotNull(device);
        assertSame(testDevice, device);
        verify(entityManager).createNativeQuery(DeviceRepository.SQL_QUERY_SINGLE);
        verify(query).setParameter(DeviceRepository.SERIAL_NUMBER_PARAM_NAME, SERIAL_NUMBER1);
        verify(query).getSingleResult();
        verify(objectCreator).objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1);
    }
       
    @Test
    public void shouldReturnDevices() throws Exception {
        TestDevice testDevice1 = new TestDevice();
        TestDevice testDevice2 = new TestDevice();
        EntityManager entityManager = mock(EntityManager.class);
        ObjectCreator<TestDevice> objectCreator = mock(ObjectCreator.class);
        Query query = mock(Query.class);
        Object[] objArray1 = new Object[]{SERIAL_NUMBER1, BYTES1, UPDATE_TIME1, NETWORK_NUMBER1};
        Object[] objArray2 = new Object[]{SERIAL_NUMBER2, BYTES2, UPDATE_TIME2, NETWORK_NUMBER2};
        List<Object[]> resultList = new ArrayList<Object[]>();
        resultList.add(objArray1);
        resultList.add(objArray2);
        
        given(entityManager.createNativeQuery(DeviceRepository.SQL_QUERY_ALL)).willReturn(query);
        given(query.setParameter(1, INIT_DATE)).willReturn(query);
        given(query.getResultList()).willReturn(resultList);
        given(objectCreator.objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1)).willReturn(testDevice1);
        given(objectCreator.objectForStuff(SERIAL_NUMBER2, BYTES2, NETWORK_NUMBER2.intValue(), UPDATE_TIME2)).willReturn(testDevice2);
        
        DeviceRepository repository = new DeviceRepository(entityManager);
        List<TestDevice> results = repository.objectsFromDb(INIT_DATE, objectCreator);
        
        assertEquals(2, results.size());
        verify(entityManager).createNativeQuery(DeviceRepository.SQL_QUERY_ALL);
        verify(query).setParameter(DeviceRepository.DATE_PARAM_NAME, INIT_DATE);
        verify(query).getResultList();
        verify(objectCreator).objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1);
        verify(objectCreator).objectForStuff(SERIAL_NUMBER2, BYTES2, NETWORK_NUMBER2.intValue(), UPDATE_TIME2);
        assertTrue(results.contains(testDevice1));
    }

    @Test
    public void shouldReturnDevicesWithNullNetworkNumber() throws Exception {
        TestDevice testDevice1 = new TestDevice();
        TestDevice testDevice2 = new TestDevice();
        EntityManager entityManager = mock(EntityManager.class);
        ObjectCreator<TestDevice> objectCreator = mock(ObjectCreator.class);
        Query query = mock(Query.class);
        Object[] objArray1 = new Object[]{SERIAL_NUMBER1, BYTES1, UPDATE_TIME1, NETWORK_NUMBER1};
        Object[] objArray2 = new Object[]{SERIAL_NUMBER2, BYTES2, UPDATE_TIME2, null};
        List<Object[]> resultList = new ArrayList<Object[]>();
        resultList.add(objArray1);
        resultList.add(objArray2);
        
        given(entityManager.createNativeQuery(DeviceRepository.SQL_QUERY_ALL)).willReturn(query);
        given(query.setParameter(1, INIT_DATE)).willReturn(query);
        given(query.getResultList()).willReturn(resultList);
        given(objectCreator.objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1)).willReturn(testDevice1);
        given(objectCreator.objectForStuff(SERIAL_NUMBER2, BYTES2, 0, UPDATE_TIME2)).willReturn(testDevice2);
        
        DeviceRepository repository = new DeviceRepository(entityManager);
        List<TestDevice> results = repository.objectsFromDb(INIT_DATE, objectCreator);
        
        assertEquals(2, results.size());
        verify(entityManager).createNativeQuery(DeviceRepository.SQL_QUERY_ALL);
        verify(query).setParameter(DeviceRepository.DATE_PARAM_NAME, INIT_DATE);
        verify(query).getResultList();
        verify(objectCreator).objectForStuff(SERIAL_NUMBER1, BYTES1, NETWORK_NUMBER1.intValue(), UPDATE_TIME1);
        verify(objectCreator).objectForStuff(SERIAL_NUMBER2, BYTES2, 0, UPDATE_TIME2);
        assertTrue(results.contains(testDevice1));
    }
}
