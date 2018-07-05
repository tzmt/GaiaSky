package gaia.cu9.ari.gaiaorbit.desktop.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default thread factory
 */
public class GaiaSkyThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(0);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePrefix;

    public GaiaSkyThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new GSThread(group, r, namePrefix + threadNumber.get(), threadNumber.getAndIncrement());
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.MAX_PRIORITY)
            t.setPriority(Thread.MAX_PRIORITY);
        return t;
    }

    public class GSThread extends Thread {
        public int index;

        public GSThread(ThreadGroup group, Runnable r, String name, int index) {
            super(group, r, name);
            this.index = index;
        }
    }
}
