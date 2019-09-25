package zedi.pacbridge.domain.repositories;

import java.sql.Timestamp;

public interface ObjectCreator<T> {
    public T objectForStuff(String serialNumber, byte[] secretKey, Integer networkNumber, Timestamp lastUpdateTime);
}