package zedi.figbridge.slapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figbridge.slapper.config.Configuration;
import zedi.figbridge.slapper.config.FigDeviceConfig;
import zedi.figbridge.slapper.utl.DeviceConglomerator;
import zedi.figbridge.slapper.utl.NameGenerator;
import zedi.figdevice.emulator.FigDevice;
import zedi.figdevice.emulator.utl.BundledReportMessageGenerator;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.NotificationCenter;

@ApplicationScoped
public class BridgeSlapper {
    private static final Logger logger = LoggerFactory.getLogger(BridgeSlapper.class.getName());

    private static int slapperNameIndex = 1;
    private static Random random = new Random(System.currentTimeMillis());
    
    private Configuration configuration;
    private DeviceConglomerator deviceConglomerator;
    private NetworkEventDispatcherManager dispatcherManager;
    private NotificationCenter notificationCenter;
    
    public BridgeSlapper() {
    }
    
    @Inject
    public BridgeSlapper(Configuration configuration, 
                         NetworkEventDispatcherManager dispatcherManager,
                         NotificationCenter notificationCenter,
                         DeviceConglomerator deviceConglomerator) {
        this.configuration = configuration;
        this.dispatcherManager = dispatcherManager;
        this.notificationCenter = notificationCenter;
        this.deviceConglomerator = deviceConglomerator;
        initialize();
    }
    
    public void start() throws IOException {
        InetSocketAddress address = configuration.getBridgeAddress();
        try {
            logger.info("Using bridge address: " + address.toString());
            Socket socket = new Socket(address.getAddress(), address.getPort());
            socket.close();
            deviceConglomerator.startAllDevices(dispatcherManager);
        } catch (ConnectException e) {
            logger.error("Unable to start simulations. Cause: " + e.toString());
            logger.error("Check that the bridge is running and that the configuration is setup correctly");
        }
    }
    
    public void stop() {
        deviceConglomerator.stopAllDevices();
    }
    
    public DeviceConglomerator getDeviceConglomerator() {
        return deviceConglomerator;
    }
    
    private void initialize() {
        NameGenerator nameGenerator = new NameGenerator(configuration.getName());
        for (FigDeviceConfig config : configuration.getDeviceConfigs()) {
            for (int i = 0; i < config.getDeviceCount(); i++) {
                String username = nameGenerator.nextName();
                BundledReportMessageGenerator messageGenerator = config.newMessageGenerator(1);
                Integer delaySeconds = 0;
                if (config.getStartDelaySeconds() > 0)
                        delaySeconds = Math.abs(random.nextInt()) % config.getStartDelaySeconds();
                FigDevice device = new FigDevice(username, notificationCenter, configuration.getBridgeAddress(), messageGenerator, config.getReconnectSeconds());
                deviceConglomerator.addDevice(device, delaySeconds);
            }
        }
    }
    
}
