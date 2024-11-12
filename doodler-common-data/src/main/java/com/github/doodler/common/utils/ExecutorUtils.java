package com.github.doodler.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

/**
 * @Description: ExecutorUtils
 * @Author: Fred Feng
 * @Date: 17/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class ExecutorUtils {

    public <T> List<T> submitAll(Executor executor, Collection<Callable<T>> callables,
            boolean failfast) {
        final List<T> results = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(callables.size());
        for (final Callable<T> callable : callables) {
            executor.execute(() -> {
                try {
                    results.add(callable.call());
                } catch (Exception e) {
                    if (failfast) {
                        throw new AsyncOperationException(e.getMessage(), e);
                    } else {
                        Logs.asyncOperations.error(e.getMessage(), e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return results;
    }

    public <T> List<T> submitAll(Executor executor, Collection<Callable<T>> callables, long timeout,
            TimeUnit timeUnit, boolean failfast) {
        final List<T> results = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(callables.size());
        for (final Callable<T> callable : callables) {
            executor.execute(() -> {
                try {
                    results.add(callable.call());
                } catch (Exception e) {
                    if (failfast) {
                        throw new AsyncOperationException(e.getMessage(), e);
                    } else {
                        Logs.asyncOperations.error(e.getMessage(), e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return results;
    }

    public <T> void submitAll(ExecutorService executor, Collection<Callable<T>> callables,
            Consumer<T> consumer) throws Exception {
        if (consumer != null) {
            List<Future<T>> futures = executor.invokeAll(callables);
            for (Future<T> future : futures) {
                consumer.accept(future.get());
            }
        } else {
            executor.invokeAll(callables);
        }
    }

    public <T> void submitAll(ExecutorService executor, Collection<Callable<T>> callables,
            long timeout, TimeUnit timeUnit, Consumer<T> consumer) throws Exception {
        if (consumer != null) {
            List<Future<T>> futures = executor.invokeAll(callables, timeout, timeUnit);
            for (Future<T> future : futures) {
                consumer.accept(future.get());
            }
        } else {
            executor.invokeAll(callables);
        }
    }

    public <T> T callInBackground(ExecutorService executor, Callable<T> callable) throws Exception {
        if (executor != null) {
            Future<T> future = executor.submit(callable);
            return future.get();
        } else {
            return callable.call();
        }
    }

    public void runInBackground(Executor executor, Runnable runnable) {
        if (executor != null) {
            executor.execute(runnable);
        } else {
            runnable.run();
        }
    }

    public static boolean isTerminated(Executor executor) {
        if (executor instanceof ExecutorService) {
            if (((ExecutorService) executor).isTerminated()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShutdown(Executor executor) {
        if (executor instanceof ExecutorService) {
            if (((ExecutorService) executor).isShutdown()) {
                return true;
            }
        }
        return false;
    }

    public static void gracefulShutdown(Executor executor, final long timeout) {
        if (!(executor instanceof ExecutorService) || isShutdown(executor)) {
            return;
        }
        final ExecutorService es = (ExecutorService) executor;
        try {
            es.shutdown();
        } catch (RuntimeException ex) {
            return;
        }
        if (!isShutdown(es)) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        es.shutdownNow();
                    } catch (RuntimeException ex) {
                        return;
                    }
                    try {
                        es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }
}
