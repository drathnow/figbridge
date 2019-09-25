package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sun.corba.se.impl.orbutil.HexOutputStream;

public class CodeBlocksBuilderTest {

    protected static final int ADDRESS = 0x380000;

    protected static final String S2RECORD1 = "S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    protected static final String S2RECORD2 = "S224380020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D1674";
    protected static final String S2RECORD3 = "S224380100E6306B7026FAED8019E887CE9898E6306B7026FA4A8000F6ED806CE8BD2604879E";
    protected static final String S2RECORD4 = "S224381440ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B8472";
    protected static final String S2RECORD5 = "S2053814600A44";
    protected static final String S2RECORD6 = "S2243845E06C3BEC82C3026E3BECF3000416C3851B84ED80ACEA026C2705CCFFFF310AED800C";

    protected static final String BINARY_DATA1 = "3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    protected static final String BINARY_DATA2 = "C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    
    @Test
    public void testParseNoncontiguousShortS2Records() throws IOException {
        List<S2Record> s2Records = new ArrayList<S2Record>();
        s2Records.add(new S2Record(S2RECORD4));
        s2Records.add(new S2Record(S2RECORD5));
        s2Records.add(new S2Record(S2RECORD6));

        CodeBlocksBuilder codeBlocksBuilder = new CodeBlocksBuilder();
        codeBlocksBuilder.buildCodeBlocksFromS2Records(s2Records);
        assertEquals(2, codeBlocksBuilder.getCodeBlocks().size());
    }

    @Test
    public void testParseNoncontiguousS2Records() throws IOException {

        List<S2Record> s2Records = new ArrayList<S2Record>();
        s2Records.add(new S2Record(S2RECORD1));
        s2Records.add(new S2Record(S2RECORD2));
        s2Records.add(new S2Record(S2RECORD3));
        
        CodeBlocksBuilder codeBlocksBuilder = new CodeBlocksBuilder();
        codeBlocksBuilder.buildCodeBlocksFromS2Records(s2Records);
        assertEquals(1, codeBlocksBuilder.getCodeBlocks().size());
    }

    @Test
    public void testParseContiguousS2Records() throws IOException {

        List<S2Record> s2Records = new ArrayList<S2Record>();
        s2Records.add(new S2Record(S2RECORD1));
        s2Records.add(new S2Record(S2RECORD2));

        CodeBlocksBuilder codeBlocksBuilder = new CodeBlocksBuilder();
        codeBlocksBuilder.buildCodeBlocksFromS2Records(s2Records);

        assertEquals(1, codeBlocksBuilder.getCodeBlocks().size());
        
        CodeBlock codeBlock = (CodeBlock)codeBlocksBuilder.getCodeBlocks().get(0);
        assertEquals(ADDRESS, codeBlock.startAddressOfData());
        assertEquals(ADDRESS + BINARY_DATA1.length() - 1, codeBlock.lastAddressOfData());
        StringWriter stringWriter = new StringWriter();
        HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
        hexOutputStream.write(codeBlock.codeData());
        hexOutputStream.close();

        assertEquals(BINARY_DATA1 + BINARY_DATA2, stringWriter.toString().toUpperCase());
    }
    
    @Test
    public void testBuildCodeBlocks() throws IOException {
        List<S2Record> s2Records = new ArrayList<S2Record>();
        s2Records.add(new S2Record(S2RECORD1));
        s2Records.add(new S2Record(S2RECORD2));
        s2Records.add(new S2Record(S2RECORD3));
        s2Records.add(new S2Record(S2RECORD4));
        s2Records.add(new S2Record(S2RECORD5));
        s2Records.add(new S2Record(S2RECORD6));
        
        CodeBlocksBuilder codeBlocksBuilder = new CodeBlocksBuilder();
        codeBlocksBuilder.buildCodeBlocksFromS2Records(s2Records);
        
        assertEquals(3, codeBlocksBuilder.getCodeBlocks().size());
        assertEquals(0x380000, codeBlocksBuilder.getStartingAddress());
    }
    
}
