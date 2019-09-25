package zedi.pacbridge.zap.reporting;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class ZapReportTest extends BaseTestCase {
    private static final String HEX_RPT = "01 00 00 00 00 00 00 00 00 00 00 00 01 FF E8 27 3D 00 14 00 14 00 00 01 00 00 00 01 01 00 00 00 02 0C 00 00 00 03 0C 00 00 00 04 05 00 00 00 05 05 00 00 00 06 06 00 00 00 07 05 00 00 00 08 01 00 00 00 09 08 00 00 00 0A 02 00 00 00 0B 05 00 00 00 0C 05 00 00 00 0D 0C 00 00 00 0E 04 00 00 00 0F 0C 00 00 00 10 05 00 00 00 11 01 00 00 00 12 03 00 00 00 13 04 00 00 00 14 02 54 E6 1A F2 40 01 40 3F C4 BE 67 BB A9 AE D4 40 3F DA 83 4F 7C 77 0D C2 40 00 00 40 29 F9 40 2C 76 A3 1A 40 00 00 40 00 40 3E 09 A0 80 40 45 40 00 00 40 D7 71 40 3F D7 36 57 6B DE 41 0A 40 66 EC 40 3F E2 FA 1D D6 4C 16 3B 40 9D 74 40 01 40 C5 40 38 37 40 4F";
    
    @Test
    public void shouldAnalyzeBytes() throws Exception {
        ZapReport.analyze(HexStringDecoder.hexStringAsBytes(HEX_RPT));
    }
}
