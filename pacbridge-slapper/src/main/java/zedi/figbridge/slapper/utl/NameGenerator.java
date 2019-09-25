package zedi.figbridge.slapper.utl;

public class NameGenerator {
    private String prefix;
    private int index;
    
    public NameGenerator(String prefix) {
        this.prefix = prefix;
        this.index = 1;
    }
    
    public String nextName() {
        return prefix + '_' + (index++);
    }
}
