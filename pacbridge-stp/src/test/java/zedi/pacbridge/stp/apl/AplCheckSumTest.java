package zedi.pacbridge.stp.apl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.CheckSum;

public class AplCheckSumTest {

    @Test
    public void shouldCalculateCheckSum2() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes("C0 29 18 95 0A 26 67 ED FA 4D 01 20 00 04 00 01 00");
        CheckSum aplCheckSum = new AplCheckSum();
        assertEquals(0x79, aplCheckSum.calculatedChecksumForByteArray(bytes));
    }
}
