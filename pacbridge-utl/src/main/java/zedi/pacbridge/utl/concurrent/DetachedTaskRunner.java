package zedi.pacbridge.utl.concurrent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.ActivityTracker;
import zedi.pacbridge.utl.DefaultActivityTracker;
import zedi.pacbridge.utl.annotations.AfterTaskFinishes;
import zedi.pacbridge.utl.annotations.BeforeTaskStarts;

public class DetachedTaskRunner<TTask extends DetachedTask> implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DetachedTaskRunner.class.getName());

    private TTask task;
    private String name;
    private ActivityTracker activityTracker;
    
    public DetachedTaskRunner(TTask task) {
        this(task, null);
    }
    
    public DetachedTaskRunner(TTask task, String name) {
        this.task = task;
        this.name = name;
        this.activityTracker = new DefaultActivityTracker();
    }
    
    public String getName() {
        return name;
    }

    public TTask getTask() {
        return task;
    }

    @Override
    public void run() {
        executeAnnotatedMethodOnTask(BeforeTaskStarts.class);
        try {
            while (task.shouldExitAfterMainLoop() == false)
                activityTracker.update();
        } finally {
            executeAnnotatedMethodOnTask(AfterTaskFinishes.class);
        }
    }
    
    private void executeAnnotatedMethodOnTask(Class<? extends Annotation> annotationClass) {
        for (Method method : task.getClass().getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                method.setAccessible(true);
                try {
                    method.invoke(task, (Object[])null);
                } catch (Exception e) {
                    String annotationName = annotationClass.getSimpleName();
                    logger.error("Unable to execute @" + annotationName + " annotated method on class " + task.getClass(), e);
                }
            }
        }
    }
}
