package zedi.pacbridge.utl.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadDumper {
    
    public static String threadDump() {
        final StringBuilder dump = new StringBuilder();
        final ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 100);
        for (ThreadInfo info : threadInfos) {
            dump.append('"')
                .append(info.getThreadName())
                .append("\" ");
            final Thread.State state = info.getThreadState();
            dump.append("\n java.lang.Thread.State: ")
                .append(state);
            final StackTraceElement[] stackTraceElements = info.getStackTrace();
            for (final StackTraceElement element : stackTraceElements) {
                dump.append("\n    at ")
                    .append(element);
            }
            dump.append("\n\n");
        }
        return dump.toString();
    }
}
