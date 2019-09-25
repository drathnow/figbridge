package zedi.pacbridge.messagedecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.messagedecoder.ui.MainWindow;
import zedi.pacbridge.net.ProtocolDecoder;
import zedi.pacbridge.utl.Notifiable;
import zedi.pacbridge.utl.Notification;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.StringUtilities;

public class MainController implements Notifiable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class.getName());

    private NotificationCenter notificationCenter;
    private ProtocolDecoder protocolDecoder;
    private Decoder decoder;

    public MainController(NotificationCenter notificationCenter) {
        this.notificationCenter = notificationCenter;
        this.decoder = new Decoder();
        notificationCenter.addObserver(this, MainWindow.DECODE_BYTES_NOTIFICATION);
    }

    @Override
    public void handleNotification(Notification notification) {
        String hexString = (String)notification.getAttachment();
        if (StringUtilities.isValidInputString(hexString))
            decoder.decodeString(hexString);
    }
}