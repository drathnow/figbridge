package zedi.pacbridge.utl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.context.ApplicationScoped;

/**
 * General purpose notification center use to post notification objects for any type of named event.  The <code>NotificationCenter</code>
 * is implemented as a singleton object.  Any object can subscribe to a notification if it knows the name. All 
 * observer objects are held in {@link WeakReference} to avoid situations where programmer forgets to remove the 
 * observer from the <code>NotificationCenter</code>
 * 
 * The NotificaionCenter mains a static pool of threads that it uses to deliver notifications asynchonously. By default, the number of
 * thread in this pool i 4 but it can be changed by setting the system property notificationCenter.maxThreadCount.  The min value
 * for this property is 1.
 * 
 */
@ApplicationScoped
public class NotificationCenter {
    public static final String MAX_THREAD_COUNT_PROPERTY_NAME = "notificationCenter.maxThreadCount";
    public static final Integer DEFAULT_THREAD_COUNT = 4;
    public static final Integer MIN_THREAD_COUNT = 1;
    
    private static IntegerSystemProperty maxTheadCountProperty = new IntegerSystemProperty(MAX_THREAD_COUNT_PROPERTY_NAME,
                                                                                           DEFAULT_THREAD_COUNT,
                                                                                           MIN_THREAD_COUNT); 
    
    private Map<String, List<WeakReference<Notifiable>>> notifiablesMap = new TreeMap<String, List<WeakReference<Notifiable>>>();
    private Lock lock = new ReentrantLock();
    private long nextDeadScanTime;

    private ExecutorService threadPool;
    
    NotificationCenter(ExecutorService threadPool) {
        this.threadPool = threadPool;
        this.nextDeadScanTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
    }
    
    public NotificationCenter() {
        this(Executors.newFixedThreadPool(maxTheadCountProperty.currentValue().intValue()));
    }
    
    /**
     * Registers a {@link Notifiable} observer object and specifies the name of the notification it has interest
     * in. 
     *  
     * @param observer - a {@link Notifiable} object 
     * @param notificationName - the name of the notification of interest.
     */
    public void addObserver(Notifiable observer, String notificationName) {
        lock.lock();
        try {
            List<WeakReference<Notifiable>> references = notifiablesMap.get(notificationName);
            if (references == null) {
                references = new ArrayList<WeakReference<Notifiable>>();
                notifiablesMap.put(notificationName, references);
            }
            references.add(new WeakReference<Notifiable>(observer));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes an observer from the specific notification.
     * 
     * @param observer - the observer to remove
     * @param notificationName - the notification name
     */
    public void removeObserver(Notifiable observer, String notificationName) {
        lock.lock();
        try {
            List<WeakReference<Notifiable>> references = notifiablesMap.get(notificationName);
            if (references != null) {
                for (Iterator<WeakReference<Notifiable>> iterator = references.iterator(); iterator.hasNext();) {
                    WeakReference<Notifiable> reference = iterator.next();
                    if (reference.get() == null)
                        iterator.remove();
                    else if (reference.get() == observer) {
                        iterator.remove();
                        break;
                    }
                }
                if (references.isEmpty())
                    notifiablesMap.remove(notificationName);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeObserver(Notifiable observer) {
        lock.lock();
        try {
            Set<String> notificationNames = new HashSet<String>(notifiablesMap.keySet());
            for (String notificationName : notificationNames)
                removeObserver(observer, notificationName);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Posts a notification.  Any {@link Notifiable} object that has expressed interest in 
     * the name event will be invoked via their <code>handleNotification</code> method.  {@link Notifiable} should
     * not do too much work such as network IO or taking out locks, during processing of the notification as it could 
     * hold up any thread that is posting the notification.
     * <br>
     * If the client of the notification is concerned about execution time, it should use the {@link postNotificationAsyn}
     * method as ensure the calling thread does not block.    
     * 
     * @param notificationName - the notification name
     */
    public void postNotification(String notificationName) {
        postNotification(notificationName, null);
    }

    /**
     * Posts a notification with an attached object.  Any {@link Notifiable} object that has expressed interest in 
     * the name event will be invoked via their <code>handleNotification</code> method.  {@link Notifiable} should
     * not do too much work such as network IO or taking out locks, during processing of the notification as it could 
     * hold up any thread that is posting the notification.
     * <br>
     * If the client of the notification is concerned about execution time, it should use the {@link postNotificationAsync}
     * method as ensure the calling thread does not block.    
     * 
     * @param notificationName - the notification name
     * @param attachment - an object with additional, notification specific information
     */
    public <T> void postNotification(String notificationName, T attachment) {
        CopyOnWriteArrayList<WeakReference<Notifiable>> copyOfList = null;
        lock.lock();
        try {
            List<WeakReference<Notifiable>> references = notifiablesMap.get(notificationName);
            if (references != null)
                copyOfList = new CopyOnWriteArrayList<WeakReference<Notifiable>>(references);
        } finally {
            lock.unlock();
        }
        
        if (copyOfList != null) {
            Notification notification = new Notification(notificationName, attachment);
            for (Iterator<WeakReference<Notifiable>> iterator = copyOfList.iterator(); iterator.hasNext();) {
                WeakReference<Notifiable> reference = iterator.next();
                if (reference.get() != null)
                    reference.get().handleNotification(notification);
            }
        }

        if (System.currentTimeMillis() >= nextDeadScanTime)
            threadPool.execute(new DeadScannRunner());
    }

    public void postNotificationAsync(String notificationName) {
        postNotificationAsync(notificationName, null);
    }

    public void postNotificationAsync(final String notificationName, final Object attachment) {
        threadPool.execute(new NotificationRunner(this, notificationName, attachment));
    }
    
    /**
     * This method scanns the list of Notifiable WeakReference object and removes those that 
     * have been garbage collected (i.e. WeakReference<>.get() == null)
     */
    private void doDeadScan() {
        this.nextDeadScanTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
        lock.lock();
        try {
            for (Iterator<List<WeakReference<Notifiable>>> listIter = notifiablesMap.values().iterator(); listIter.hasNext(); ) {
                for (Iterator<WeakReference<Notifiable>> refIter = listIter.next().iterator(); refIter.hasNext(); ) {
                    if (refIter.next().get() == null)
                        refIter.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    class DeadScannRunner implements Runnable {
        @Override
        public void run() {
            doDeadScan();
        }
    }    
}
