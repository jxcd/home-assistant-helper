package com.me.project.hah.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author cwj
 * @date 2021/9/27
 */
public class ThreadUtil {
    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    private static final ThreadFactory SIMPLE_THREAD_FACTORY = new SimpleThreadFactory();

    public static final long SLEEP_SHORT = 1000;
    public static final long SLEEP_MIDDLE = 5 * 1000;
    public static final long SLEEP_LONG = 10 * 1000;

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(8, SIMPLE_THREAD_FACTORY);

    /**
     * 公共任务调度线程池
     * 如果需要调度的任务过多, 建议加大线程池容量
     */
    private static final ScheduledThreadPoolExecutor SCHEDULED_POOL =
            new ScheduledThreadPoolExecutor(8, new PrefixThreadFactory("global-timer-"));
    private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = (t, e) ->
            log.warn(String.format("find not catch exceptions: , threadName: %s, e: %s", t.getName(), e.getMessage()), e);

    public static Thread createThread(Runnable runnable) {
        return SIMPLE_THREAD_FACTORY.newThread(runnable);
    }

    public static Thread createThread(String name, Runnable runnable) {
        Thread thread = SIMPLE_THREAD_FACTORY.newThread(runnable);
        thread.setName(name);
        return thread;
    }

    public static void sleepShort() {
        sleep(SLEEP_SHORT);
    }

    public static void sleepMiddle() {
        sleep(SLEEP_MIDDLE);
    }

    public static void sleepLong() {
        sleep(SLEEP_LONG);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn("sleep {}ms exception", millis, e);
        }
    }

    public static void execute(Runnable runnable) {
        EXECUTORS.execute(runnable);
    }

    /**
     * 目的是为了替代Timer
     * 使用公共线程池进行任务调度, 适合调度执行时间较短的任务
     */
    public static void registerTask(String name, Runnable task, long initialDelay, long fixedDelay) {
        SCHEDULED_POOL.scheduleWithFixedDelay(() -> ThreadUtil.runWithThreadName(name, task), initialDelay, fixedDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * 目的是为了替代Timer
     * 新建一个线程池进行任务调度, 适合调度执行时间较长, 或环境较复杂, 或运行相对独立的任务
     */
    public static void scheduleTask(String name, Runnable task, long initialDelay, long fixedDelay) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new PrefixThreadFactory(name + "-"));
        executor.scheduleWithFixedDelay(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("schedule task find exception, e: {}", e.getMessage(), e);
            }
        }, initialDelay, fixedDelay, TimeUnit.MILLISECONDS);
    }

    /**
     * 用指定的线程名执行一段代码
     *
     * @param name     指定的线程名
     * @param runnable 要执行的代码片段
     */
    public static void runWithThreadName(String name, Runnable runnable) {
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        currentThread.setName(name);
        try {
            runnable.run();
        } catch (Exception e) {
            // 下面既然抛出异常, 此处按规范不应该打印异常堆栈, 避免重复输出异常信息
            // 但是实际中发现有时候外部没有打印堆栈, 也没有相应异常处理, 所以这里还是加了打印异常堆栈方便排查问题
            log.error("run with thread find exception, e: {}", e.getMessage(), e);
            throw e;
        } finally {
            currentThread.setName(threadName);
        }
    }

    public static Future<?> asyncWithThreadName(String name, Runnable runnable) {
        return asyncWithThreadName(name, runnable, EXECUTORS);
    }

    public static Future<?> asyncWithThreadName(String name, Runnable runnable, ExecutorService executorService) {
        return executorService.submit(() -> runWithThreadName(name, runnable));
    }

    public static void waitFor(Supplier<Boolean> actual, Supplier<String> message) {
        waitFor(Boolean.TRUE, actual, message, 1000L, 30000L);
    }

    public static <T> void waitFor(final T expected, final Supplier<T> actual, Supplier<String> message, long sleep, long timeout) {
        long s = System.currentTimeMillis();
        while (!Objects.equals(expected, actual.get())) {
            String info = message.get();
            long e = System.currentTimeMillis();
            if (e - s > timeout) {
                throw new RuntimeException("wait timeout: " + info);
            }
            log.debug(info);
            sleep(sleep);
        }
    }

    public static class SimpleThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_HANDLER);
            return thread;
        }
    }

    public static class PrefixThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger atomic = new AtomicInteger();

        public PrefixThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return ThreadUtil.createThread(prefix + atomic.getAndIncrement(), r);
        }
    }

}

