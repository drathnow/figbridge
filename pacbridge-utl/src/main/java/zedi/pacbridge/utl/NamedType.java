package zedi.pacbridge.utl;

import java.io.Serializable;
import java.util.Objects;

public abstract class NamedType implements Serializable {
    
    private String name;
    private Integer number;
    
    protected NamedType(String name, Integer number) {
        this.name = name;
        this.number = number;
    }
    
    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == null ? false : 
            (number.equals(((NamedType)obj).number) && name.equals(((NamedType)obj).name));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }
    
    public String toString() {
        return name + '(' + number + ')';
    }
}
