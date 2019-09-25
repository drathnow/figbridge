package zedi.pacbridge.utl.concurrent;

public class Atomic<T> {
    private T atomicVariable;
    
    public void set(T atomicVariable) {
        this.atomicVariable = atomicVariable;
    }
    
    public synchronized T get() {
        return atomicVariable;
    }
}
