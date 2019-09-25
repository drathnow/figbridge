package zedi.pacbridge.gdn.otad;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class CodeBlocksBuilder {

    private int startingAddress = Integer.MAX_VALUE;
    private CodeBlock currentCodeBlock;
    private Map<Integer, CodeBlock> sortedCodeBlocks = new TreeMap<Integer, CodeBlock>();

    public void buildCodeBlocksFromS2Records(List<S2Record> s2Records) {
        reset();
        for (S2Record record : s2Records)
            addDataToCurrentCodeBlock(record.getAddress(), record.getData());
    }

    public List<CodeBlock> getCodeBlocks() {
        return new ArrayList<CodeBlock>(sortedCodeBlocks.values());
    }

    public int getStartingAddress() {
        return startingAddress;
    }
    
    private void reset() {
        startingAddress = 0;
        sortedCodeBlocks.clear();
        startingAddress = Integer.MAX_VALUE;
    }

    private void addDataToCurrentCodeBlock(int anAddress, byte[] bytes) {
        startingAddress = Math.min(startingAddress, anAddress);
        createCodeBlockWithAddress(anAddress);
        int bytesConsumed = currentCodeBlock.addCodeData(anAddress, bytes);
        if (bytesConsumed != bytes.length) {
            createCodeBlockWithAddress(anAddress + bytesConsumed);
            currentCodeBlock.addCodeData(anAddress + bytesConsumed, bytes, bytesConsumed, bytes.length - bytesConsumed);
        }
    }

    private void createCodeBlockWithAddress(int address) {
        Integer mungedAddress = new Integer(address & ~0x3ff);
        currentCodeBlock = (CodeBlock)sortedCodeBlocks.get(mungedAddress);
        if (currentCodeBlock == null) {
            currentCodeBlock = new CodeBlock(address);
            sortedCodeBlocks.put(mungedAddress, currentCodeBlock);
        }
    }

    public Map<Integer, CodeBlock> getSortedCodeBlocks() {
        return sortedCodeBlocks;
    }
}
