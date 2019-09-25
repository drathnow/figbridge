package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import zedi.pacbridge.utl.Utilities;

public class GdnIoPointClassTest {

    @Test
    public void testIOPointClassForClassNumber() throws Exception {
        assertEquals(GdnIoPointClass.ioPointClassForClassNumber(GdnIoPointClass.System.getNumber()), GdnIoPointClass.System);
        assertEquals(GdnIoPointClass.ioPointClassForClassNumber(GdnIoPointClass.Rtu.getNumber()), GdnIoPointClass.Rtu);
        assertEquals(GdnIoPointClass.ioPointClassForClassNumber(GdnIoPointClass.IoBoard.getNumber()), GdnIoPointClass.IoBoard);
        assertEquals(GdnIoPointClass.ioPointClassForClassNumber(GdnIoPointClass.Network.getNumber()), GdnIoPointClass.Network);
        assertEquals(GdnIoPointClass.ioPointClassForClassNumber(GdnIoPointClass.Application.getNumber()), GdnIoPointClass.Application);
        assertNull(GdnIoPointClass.ioPointClassForClassNumber(0));
        assertNull(GdnIoPointClass.ioPointClassForClassNumber(99));
    }
    
    @Test
    public void shouldSerialize() throws Exception {
        byte[] bytes = Utilities.objectAsByteArrays(GdnIoPointClass.System);
        Utilities.byteArrayAsObject(bytes);
    }
}
