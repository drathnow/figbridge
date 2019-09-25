package zedi.pacbridge.app.devices;

import java.util.Date;


public interface DeviceCacheUpdateDelegate {

    String JNDI_NAME = "java:global/DeviceCacheUpdateDelegate";

    Date getLatestUpdateTime();

    void primeCache();

    Date checkForUpdates();

}