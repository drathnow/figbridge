package zedi.pacbridge.app.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ConnectionGarbageCollector {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionGarbageCollector.class.getName());

    private BlockingQueue<Connection> connections;
    private Thread thread;
    
    @Inject
    public ConnectionGarbageCollector(ThreadFactory threadFactory) {
        connections = new LinkedBlockingDeque<>();
        thread = threadFactory.newThread(new CollectionRunner());
        thread.setDaemon(true);
        thread.start();
    }

    public ConnectionGarbageCollector() {
    }
    
    public void queueForCleanup(Connection connection) {
        connections.add(connection);
    }
    
    class CollectionRunner implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                        Connection connection = connections.take();
                        try {
                            connection.destroy();
                        } catch (Exception e) {
                            logger.error("Unhandled exception from Connection.destroy()", e);
                        }
                }
            } catch (InterruptedException e) {
            }
        }
        
    }
}
