package zedi.pacbridge.domain.repositories;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class DeviceRepository {
    public static final String JNDI_NAME = "java:global/DeviceRepository";
    public static final String DATE_PARAM_NAME = "utc_date";
    public static final String SERIAL_NUMBER_PARAM_NAME = "serial_number";
    
    private static final Integer SN_POS = 0;
    private static final Integer HASH_POS = 1;
    private static final Integer TIME_POS = 2;
    private static final Integer NETNUM_POS = 3;
    private static final String COMMON_QUERY = "select smartalek.serialnumber, smartalek.hashtable, smartalek.updatetimeutc, networknumber.networknumber"
                                            + " from developer.smartalek"
                                            + " left outer join developer.networkNumber on networkNumber.networkNumber_id = smartalek.networkNumber_id"
                                            + " where smartalek.smartalektype_id = 15 and smartalek.hashtable is not null";
    
    public static final String SQL_QUERY_ALL = COMMON_QUERY + " and smartalek.updatetimeutc > :" + DATE_PARAM_NAME;
    public static final String SQL_QUERY_SINGLE = COMMON_QUERY + " and smartalek.serialnumber = :" + SERIAL_NUMBER_PARAM_NAME;

    @PersistenceContext(unitName = "pacbridge-domain")
    private EntityManager entityManager;

    public DeviceRepository() {
    }
    
    public DeviceRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> objectsFromDb(Date sinceLastUpdateDate, ObjectCreator<T> objectCreator) {
        List<T> results = new ArrayList<T>();
        Query query = entityManager.createNativeQuery(SQL_QUERY_ALL);
        query.setParameter(DATE_PARAM_NAME, sinceLastUpdateDate);
        List<Object[]> resultList = query.getResultList();
        for (Iterator<Object[]> iter = resultList.iterator(); iter.hasNext(); ) {
            Object[] os = iter.next();
            Integer networkNumber = os[NETNUM_POS] == null ? 0 : ((BigDecimal)os[NETNUM_POS]).intValue();
            T device = objectCreator.objectForStuff((String)os[SN_POS], 
                                                     (byte[])os[HASH_POS], 
                                                     networkNumber, 
                                                     ((Timestamp)os[TIME_POS]));
            if (device != null)
                results.add(device);
        }
        return results;
    }

    public <T> T objectWithSerialNumber(String serialNumber, ObjectCreator<T> objectCreator) throws MoreThanOneException {
        T result = null;
        Query query = entityManager.createNativeQuery(SQL_QUERY_SINGLE);
        query.setParameter(SERIAL_NUMBER_PARAM_NAME, serialNumber);
        try {
            Object[] os = (Object[])query.getSingleResult();
            if (os != null) {
                Integer networkNumber = os[NETNUM_POS] == null ? 0 : ((BigDecimal)os[NETNUM_POS]).intValue();
                result = objectCreator.objectForStuff((String)os[SN_POS], 
                                                      (byte[])os[HASH_POS], 
                                                      networkNumber, 
                                                      ((Timestamp)os[TIME_POS]));
            }
        } catch (NoResultException e) {
        }
        return result;
    }
}