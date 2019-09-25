package zedi.figbridge.slapper.utl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import zedi.figdevice.emulator.FigDevice;
import zedi.figdevice.emulator.PublishedReportAttachement;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;

public class DeviceConglomerator implements Notifiable {
    private EventIdTracker eventIdTracker;
    private Map<String, FigDevice> deviceMap;
    private Map<FigDevice, Integer> startDelayMap;
    private GlobalScheduledExecutor globalScheduledExecutor;
    
    public DeviceConglomerator() {
        this.eventIdTracker = new EventIdTracker();
        this.deviceMap = Collections.synchronizedMap(new TreeMap<String, FigDevice>());
        this.startDelayMap = new TreeMap<FigDevice, Integer>();
    }
    
    @Inject
    public DeviceConglomerator(GlobalScheduledExecutor globalScheduledExecutor, NotificationCenter notificationCenter) {
        this();
        this.globalScheduledExecutor = globalScheduledExecutor;
        notificationCenter.addObserver(this, FigDevice.REPORT_SENT_NOTIFICATION);
    }

    public void addDevice(FigDevice figDevice, Integer startDelaySeconds) {
        deviceMap.put(figDevice.getUsername(), figDevice);
        startDelayMap.put(figDevice, startDelaySeconds);
        eventIdTracker.addDevice(figDevice.getUsername());
    }
    
    public void stopAllDevices() {
        synchronized (deviceMap) {
            for (final FigDevice device : deviceMap.values())
                device.stop();
        }
    }
    
    public void removeEventIdForDeviceName(Long eventId, String deviceName) {
        eventIdTracker.removeEventIdForDeviceName(eventId, deviceName);
    }
    
    @Override
    public void handleNotification(Notification notification) {
        PublishedReportAttachement attachment = notification.getAttachment();
        eventIdTracker.addEventIdForDeviceName(attachment.getEventId(), attachment.getUsername());
    }

    public void startAllDevices(final NetworkEventDispatcherManager manager) throws IOException {
        synchronized (deviceMap) {
            for (final FigDevice device : deviceMap.values()) {
                Integer delaySeconds = startDelayMap.get(device);
                if (delaySeconds > 0)
                    globalScheduledExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            device.start(manager);
                        }
                    }, delaySeconds);
                else 
                    device.start(manager);
            }
        }
    }

    public long getTotalReportCount() {
        long total = 0;
        for (FigDevice device : deviceMap.values())
            total += device.getTotalReportsSent();
        return total;
    }
    
    public long getDelinquentReports() {
        return eventIdTracker.getDelinquentReports();
    }

}
