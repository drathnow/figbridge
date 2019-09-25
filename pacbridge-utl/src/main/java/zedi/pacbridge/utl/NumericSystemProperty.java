package zedi.pacbridge.utl;

import java.io.Serializable;
import java.lang.reflect.Constructor;

public abstract class NumericSystemProperty implements Serializable {
    protected String propertyName;
    protected Class<?> clazz;
    protected Number minValue;
    protected Number maxValue;
    protected Number defaultValue;

    public NumericSystemProperty(String propertyName, Class<?> clazz, Number defaultValue, Number minValue, Number maxValue) {
        this.propertyName = propertyName;
        this.clazz = clazz;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
    }

    protected Constructor<?> constructorForClass() throws NoSuchMethodException {
        return clazz.getConstructor(new Class[] {String.class});
    }
    
}