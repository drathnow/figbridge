package zedi.pacbridge.utl.strategies;

public class WaitForEverStrategy implements WaitStrategy {

    @Override
    public void doWait() throws InterruptedException {
        synchronized (this) {
            wait();
        }
    }
    
}
