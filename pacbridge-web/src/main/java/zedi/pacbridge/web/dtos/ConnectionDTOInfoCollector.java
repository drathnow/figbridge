package zedi.pacbridge.web.dtos;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.ConnectionInfoCollector;

public class ConnectionDTOInfoCollector implements ConnectionInfoCollector<ConnectionDTO> {
    public static final String DEFAULT_ACTIVITY_STRING = "<None>";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    @Override
    public ConnectionDTO collectInfo(Connection connection) {
        String lastActivityTime = DEFAULT_ACTIVITY_STRING; 
        if (connection.getLastActivityTime() != null)
            lastActivityTime = dateFormat.format(new Date(connection.getLastActivityTime()));
        String ipAddress = "Unresolved";
        InetSocketAddress remoteAddress = connection.getRemoteAddress();
        if (remoteAddress != null) {
            InetAddress inetAddress = remoteAddress.getAddress();
            if (inetAddress != null)
                ipAddress = inetAddress.getHostAddress();
        }
        return new ConnectionDTO(connection.getSiteAddress().getAddress(), 
                                 connection.getSiteAddress().getNetworkNumber(),
                                 connection.getBytesTransmitted(),
                                 connection.getBytesReceived(),
                                 lastActivityTime,
                                 ipAddress,
                                 connection.getFirmwareVersion());
    }
}
