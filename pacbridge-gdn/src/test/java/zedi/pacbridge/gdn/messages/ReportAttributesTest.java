package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class ReportAttributesTest extends BaseTestCase {
    protected ReportAttributes reportAttributes;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;
    protected DataInputStream dataInputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        reportAttributes = new ReportAttributes();
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @Test
    public void tearDown() throws Exception {
        reportAttributes = null;
        super.tearDown();
    }
    
    @Test
    public void testRawValue() {
        assertEquals(0x20, ReportAttributes.rawValue(ValueType.Extended.getNumber(), GdnReasonCode.Scheduled.getNumber()));
        assertEquals(0x04, ReportAttributes.rawValue(ValueType.Standard.getNumber(), GdnReasonCode.AlarmModify.getNumber()));
    }

    @Test
    public void testConstructor() throws Exception {
        reportAttributes = new ReportAttributes(0x20);
        assertEquals(GdnReasonCode.Scheduled, reportAttributes.getReasonCode());
        assertEquals(ValueType.Extended, reportAttributes.getValueType());
    }

    @Test
    public void testCorrectValue() throws IOException {
        byteArrayOutputStream.reset();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{0x20});
        reportAttributes.deserialize(byteBuffer);
        assertEquals(GdnReasonCode.Scheduled, reportAttributes.getReasonCode());
        assertEquals(ValueType.Extended, reportAttributes.getValueType());
        reportAttributes.toString();
    }

    @Test
    public void testSerializeInput() throws IOException {
        encodedInputTest(GdnReasonCode.Scheduled.getNumber(), ValueType.Extended.getNumber());
        encodedInputTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodedInputTest(GdnReasonCode.DemandPoll.getNumber(), ValueType.Standard.getNumber());
        encodedInputTest(GdnReasonCode.IOModify.getNumber(), ValueType.Standard.getNumber());
        encodedInputTest(GdnReasonCode.IOWrite.getNumber(), ValueType.Extended.getNumber());
        encodedInputTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodedInputTest(GdnReasonCode.AlarmTrigger.getNumber(), ValueType.Standard.getNumber());
        encodedInputTest(GdnReasonCode.Reserved.getNumber(), ValueType.Standard.getNumber());
    }


    @Test
    public void shouldDeserializeFromByteBuffer() throws IOException {
        encodeFromByteBufferTest(GdnReasonCode.Scheduled.getNumber(), ValueType.Extended.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.DemandPoll.getNumber(), ValueType.Standard.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.IOModify.getNumber(), ValueType.Standard.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.IOWrite.getNumber(), ValueType.Extended.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.AlarmTrigger.getNumber(), ValueType.Standard.getNumber());
        encodeFromByteBufferTest(GdnReasonCode.Reserved.getNumber(), ValueType.Standard.getNumber());
    }

    @Test
    public void testSerializeOutput() throws IOException {
        encodedOutputTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodedOutputTest(GdnReasonCode.Scheduled.getNumber(), ValueType.Extended.getNumber());
        encodedOutputTest(GdnReasonCode.DemandPoll.getNumber(), ValueType.Standard.getNumber());
        encodedOutputTest(GdnReasonCode.IOModify.getNumber(), ValueType.Standard.getNumber());
        encodedOutputTest(GdnReasonCode.IOWrite.getNumber(), ValueType.Extended.getNumber());
        encodedOutputTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodedOutputTest(GdnReasonCode.AlarmTrigger.getNumber(), ValueType.Standard.getNumber());
        encodedOutputTest(GdnReasonCode.Reserved.getNumber(), ValueType.Standard.getNumber());
    }

    @Test
    public void shouldSerializeToByteBuffer() throws IOException {
        encodeToByteBufferTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodeToByteBufferTest(GdnReasonCode.Scheduled.getNumber(), ValueType.Extended.getNumber());
        encodeToByteBufferTest(GdnReasonCode.DemandPoll.getNumber(), ValueType.Standard.getNumber());
        encodeToByteBufferTest(GdnReasonCode.IOModify.getNumber(), ValueType.Standard.getNumber());
        encodeToByteBufferTest(GdnReasonCode.IOWrite.getNumber(), ValueType.Extended.getNumber());
        encodeToByteBufferTest(GdnReasonCode.AlarmModify.getNumber(), ValueType.Standard.getNumber());
        encodeToByteBufferTest(GdnReasonCode.AlarmTrigger.getNumber(), ValueType.Standard.getNumber());
        encodeToByteBufferTest(GdnReasonCode.Reserved.getNumber(), ValueType.Standard.getNumber());
    }

    private void encodedInputTest(int reasonCode, int valueType) throws IOException {
        byteArrayOutputStream.reset();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{encode(reasonCode, valueType)});
        reportAttributes.deserialize(byteBuffer);
        assertEquals(GdnReasonCode.reascodeForReasonNumber(reasonCode), reportAttributes.getReasonCode());
        assertEquals(ValueType.valueTypeForTypeNumber(valueType), reportAttributes.getValueType());
    }

    private static void encodeFromByteBufferTest(int reasonCode, int valueType) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(new byte[]{encode(reasonCode, valueType)});
        byteBuffer.flip();

        ReportAttributes reportAttributes = new ReportAttributes();
        reportAttributes.deserialize(byteBuffer);
        
        assertEquals(GdnReasonCode.reascodeForReasonNumber(reasonCode), reportAttributes.getReasonCode());
        assertEquals(ValueType.valueTypeForTypeNumber(valueType), reportAttributes.getValueType());
    }

    private void encodeToByteBufferTest(int reasonCode, int valueType) throws IOException {
        ReportAttributes reportAttributes = new ReportAttributes();
        reportAttributes.setReasonCode(GdnReasonCode.reascodeForReasonNumber(reasonCode));
        reportAttributes.setValueType(ValueType.valueTypeForTypeNumber(valueType));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        reportAttributes.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(encode(reasonCode, valueType), Unsigned.getUnsignedByte(byteBuffer));
    }
    
    private void encodedOutputTest(int reasonCode, int valueType) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        reportAttributes.setReasonCode(GdnReasonCode.reascodeForReasonNumber(reasonCode));
        reportAttributes.setValueType(ValueType.valueTypeForTypeNumber(valueType));
        reportAttributes.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(encode(reasonCode, valueType), Unsigned.getUnsignedByte(byteBuffer));
    }

    protected static byte encode(int reasonCode, int valueType) {
        return (byte)ReportAttributes.rawValue(valueType, reasonCode);
    }
}