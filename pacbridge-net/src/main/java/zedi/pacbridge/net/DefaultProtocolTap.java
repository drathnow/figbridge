package zedi.pacbridge.net;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.HexStringEncoder;

public class DefaultProtocolTap implements LayerTap {
    private static Logger logger = LoggerFactory.getLogger(DefaultProtocolTap.class.getName());

    private String protocolName;
    
    public DefaultProtocolTap(String protocolName) {
        this.protocolName = protocolName;
    }

    @Override
    public void bytesSent(ByteBuffer byteBuffer) {
        logger.info(protocolName + " trx> " + HexStringEncoder.bytesAsHexString(byteBuffer));
    }

    @Override
    public void bytesReceived(ByteBuffer byteBuffer) {
        logger.info(protocolName + " rcv> " + HexStringEncoder.bytesAsHexString(byteBuffer));
    }
}
