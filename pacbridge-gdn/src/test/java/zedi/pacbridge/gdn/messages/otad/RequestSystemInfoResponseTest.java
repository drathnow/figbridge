package zedi.pacbridge.gdn.messages.otad;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class RequestSystemInfoResponseTest extends BaseTestCase {
    private final static Integer APPLICATIONID = 12;
    private final static Integer APPLICATIONVERSION = 13;
    private final static Integer APPLICATIONBUILD = 14;
    private final static Integer NETWORKID = 17;
    private final static Integer NETWORKVERSION = 18;
    private final static Integer IDENTIFIER = 42;

    private static final Integer PLATFORMID = 1;
    private static final Integer RTUID = 5;
    private static final Integer RTUVERSION = 6;
    private static final Integer FLASHSIZE = 9;
    private static final byte CODEMAP[] = new byte[]{0x02, 0x03};

    @Test
    public void shouldDeserializeResponseWithNoError() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put((byte)0x01);
        byteBuffer.putShort((short)(RequestSystemInfoResponse.FIXED_SIZE + CODEMAP.length));
        byteBuffer.put(PLATFORMID.byteValue());
        byteBuffer.put(APPLICATIONID.byteValue());
        byteBuffer.putShort(APPLICATIONVERSION.shortValue());
        byteBuffer.putShort(APPLICATIONBUILD.shortValue());
        byteBuffer.putInt(RTUID);
        byteBuffer.putShort(RTUVERSION.shortValue());
        byteBuffer.putInt(NETWORKID);
        byteBuffer.putShort(NETWORKVERSION.shortValue());
        byteBuffer.put(FLASHSIZE.byteValue());
        byteBuffer.putShort(IDENTIFIER.shortValue());
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)0);
        byteBuffer.put(CODEMAP);
        byteBuffer.flip();
        
        RequestSystemInfoResponse response = RequestSystemInfoResponse.requestSystemInfoResponseFromByteBuffer(byteBuffer);
        assertEquals(PLATFORMID, response.getPlatformId());
        assertEquals(APPLICATIONID, response.getApplicationId());
        assertEquals(APPLICATIONVERSION, response.getApplicationVersion());
        assertEquals(APPLICATIONBUILD, response.getApplicationBuild());
        assertEquals(RTUID, response.getRtuId());
        assertEquals(RTUVERSION, response.getRtuVersion());
        assertEquals(NETWORKID, response.getNetworkId());
        assertEquals(NETWORKVERSION, response.getNetworkVersion());
        assertEquals(FLASHSIZE, response.getFlashSize());
        assertEquals(IDENTIFIER, response.getIdentifier());
    }
    
    @Test
    public void shouldDeserializeResponseWithError() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.put((byte)0x21);
        byteBuffer.putShort((short)3);
        byteBuffer.flip();
        
        RequestSystemInfoResponse response = RequestSystemInfoResponse.requestSystemInfoResponseFromByteBuffer(byteBuffer);
        assertEquals(ErrorCode.InvalidCommandLength, response.getErrorCode());
    }
}
