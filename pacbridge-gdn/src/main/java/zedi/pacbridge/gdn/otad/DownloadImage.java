package zedi.pacbridge.gdn.otad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DownloadImage {
    protected int startingAddress = Integer.MAX_VALUE;

    S0Record s0Record;
    List<CodeBlock> codeBlocks = new ArrayList<CodeBlock>();
    
    public DownloadImage() {
    }

    public DownloadImage(File downloadImageFile) throws IOException {
        this(new FileInputStream(downloadImageFile));
    }

    public DownloadImage(InputStream inputStream) throws IOException {
        List<S2Record> s2Records = s2recordsFromInputStream(inputStream);
        CodeBlocksBuilder codeBlocksBuilder = new CodeBlocksBuilder();
        codeBlocksBuilder.buildCodeBlocksFromS2Records(s2Records);
        codeBlocks = codeBlocksBuilder.getCodeBlocks();
        startingAddress = codeBlocksBuilder.getStartingAddress();
    }

    public CodeMap getCodeMap() {
        CodeMap codeMap = new CodeMap();
        for (CodeBlock codeBlock : getCodeBlocks()) {
            int startByte = codeBlock.startingByteOfCodeMap();
            int[] masks = codeBlock.codeMaps();
            codeMap.addMapByte(masks[0], startByte);
        }
        return codeMap;
    }
    
    public CodeBlockMap codeMap() {
        CodeBlockMap codeMap = new CodeBlockMap();
        for (Iterator<CodeBlock> iter = getCodeBlocks().iterator(); iter.hasNext();)
            addCodeBlockToCodeMap(iter.next(), codeMap);
        return codeMap;
    }

    public int getStartingAddress() {
        return startingAddress;
    }
    
    public int numberOfCodeBlocks() {
        return getCodeBlocks().size();
    }

    public String formattedVersionString() {
        return s0Record.getFormattedVersionString();
    }
    
    protected void addCodeBlockToCodeMap(CodeBlock codeBlock, CodeBlockMap codeMap) {
        int startByte = codeBlock.startingByteOfCodeMap();
        int[] masks = codeBlock.codeMaps();
        codeMap.setBitsInByte(masks[0], true, startByte);
    }

    public String versionString() {
        return s0Record.getFormattedVersionString();
    }

    public List<CodeBlock> getCodeBlocks() {
        return new ArrayList<CodeBlock>(codeBlocks);
    }

    public int getVersionNumber() {
        return s0Record.getVersionNumber();
    }

    public int getBuildNumber() {
        return s0Record.getBuildNumber();
    }
    
    private List<S2Record> s2recordsFromInputStream(InputStream inputStream) throws IOException {
        List<S2Record> s2Records = new ArrayList<S2Record>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String nextLine;
        while ((nextLine = bufferedReader.readLine()) != null) {
            if (!nextLine.startsWith("S"))
                throw new IllegalArgumentException("Invalid S2 record.  Record does not start with 'S':" + nextLine);
            switch (nextLine.charAt(1)) {
                case '0' :
                    s0Record = new S0Record(nextLine);
                    break;
                case '2' :
                    s2Records.add(new S2Record(nextLine));
                    break;
                case '8' :
                    break;
                default :
                    throw new IllegalArgumentException("Invalid S record type encountered: " + nextLine.charAt(1));
            }
        }
        return s2Records;
    }

}
