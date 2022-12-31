package cn.smilex.vueblog.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author smilex
 */
public class CounterThreadFactory implements ThreadFactory {
    private final LongAdder counter;
    private final String name;

    public CounterThreadFactory(String name) {
        this.name = name;
        this.counter = new LongAdder();
    }

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        this.counter.increment();
        return new Thread(runnable, String.format("%s-%d", name, counter.longValue()));
    }
}
