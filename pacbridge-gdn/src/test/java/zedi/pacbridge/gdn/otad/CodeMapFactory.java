package zedi.pacbridge.gdn.otad;


public class CodeMapFactory {
    public CodeMap codeMapForCodeMapBytes(byte[] codeMapBytes) {
        return new CodeMap(codeMapBytes);
    }
}
