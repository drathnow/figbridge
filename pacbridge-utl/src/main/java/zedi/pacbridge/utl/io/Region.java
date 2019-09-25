package zedi.pacbridge.utl.io;

public class Region {
    private int offset;
    private int length;

    public Region(int offset, int length) {
        super();
        this.offset = offset;
        this.length = length;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
}
