package zedi.pacbridge.utl.concurrent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import zedi.pacbridge.utl.annotations.AfterTaskFinishes;
import zedi.pacbridge.utl.annotations.BeforeTaskStarts;

public class DetachedTaskRunnerTest {

    @Test
    public void shouldHandleFinishExceptionsFromAnnotatedMethod() throws Exception {
        MyDetachedTask task = new MyDetachedTask();
        task.finishException = new RuntimeException();
        
        DetachedTaskRunner<MyDetachedTask> runner = new DetachedTaskRunner<DetachedTaskRunnerTest.MyDetachedTask>(task);
        
        try {
            runner.run();
        } catch (Throwable e) {
            fail("This shouldn't happen");
        }
    }

    @Test
    public void shouldHandleStartExceptionsFromAnnotatedMethod() throws Exception {
        MyDetachedTask task = new MyDetachedTask();
        task.startException = new RuntimeException();
        
        DetachedTaskRunner<MyDetachedTask> runner = new DetachedTaskRunner<DetachedTaskRunnerTest.MyDetachedTask>(task);
        
        try {
            runner.run();
        } catch (Throwable e) {
            fail("This shouldn't happen");
        }
    }
    
    @Test
    public void shouldCallAnnotatedMethods() {
        MyDetachedTask task = new MyDetachedTask();
        
        DetachedTaskRunner<MyDetachedTask> runner = new DetachedTaskRunner<DetachedTaskRunnerTest.MyDetachedTask>(task);
        runner.run();
        
        assertTrue(task.beforeTaskStarts);
        assertTrue(task.afterTaskFinishes);
    }
    
    
    private class MyDetachedTask implements DetachedTask {

        boolean beforeTaskStarts;
        boolean afterTaskFinishes;
        RuntimeException startException;
        RuntimeException finishException;
        
        @BeforeTaskStarts
        public void executionStarting() {
            if (startException != null)
                throw startException;
            beforeTaskStarts = true;
        }

        @Override
        public boolean shouldExitAfterMainLoop() {
            return true;
        }

        @AfterTaskFinishes
        public void executionTerminating() {
            if (finishException != null)
                throw finishException;
            afterTaskFinishes = true;
        }
        
    }

}
