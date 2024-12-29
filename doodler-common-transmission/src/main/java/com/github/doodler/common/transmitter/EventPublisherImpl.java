package com.github.doodler.common.transmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.apache.commons.collections4.CollectionUtils;
import com.github.doodler.common.utils.ExecutorUtils;
import com.github.doodler.common.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: EventPublisherImpl
 * @Author: Fred Feng
 * @Date: 29/12/2024
 * @Version 1.0.0
 */
@Slf4j
public class EventPublisherImpl<T>
        implements BiConsumer<Flow.Subscriber<? super T>, Throwable>, EventPublisher<T> {

    private final SubmissionPublisher<T> publisher;
    private final int requestFetchSize;
    private final long timeout;
    private final Buffer<T> buffer;
    private final BufferCleaner bufferCleaner;

    public EventPublisherImpl(Executor executor, int maxBufferCapacity, int requestFetchSize,
            long timeout, Buffer<T> buffer, long bufferCleanInterval) {
        this.publisher = new SubmissionPublisher<>(executor, maxBufferCapacity, this);
        this.requestFetchSize = requestFetchSize;
        this.timeout = timeout;
        this.buffer = buffer;
        this.bufferCleaner = new BufferCleaner(bufferCleanInterval);
    }

    @Override
    public int subscribe(Collection<EventSubscriber<T>> subscribers) {
        if (CollectionUtils.isNotEmpty(subscribers)) {
            for (EventSubscriber<T> subscriber : subscribers) {
                publisher.subscribe(new EventSubscriberWrapper<T>(requestFetchSize, subscriber));
            }
        }
        return publisher.getNumberOfSubscribers();
    }

    @Override
    public void publish(T event) {
        publisher.offer(event, timeout, TimeUnit.MILLISECONDS, (sub, droppedItem) -> {
            if (buffer != null) {
                buffer.put(event);
                return true;
            }
            return false;
        });
    }

    @Override
    public void destroy() {
        if (bufferCleaner != null) {
            bufferCleaner.stop();
        }
        if (buffer != null) {
            buffer.destroy();
        }
    }

    @Override
    public void accept(Subscriber<? super T> s, Throwable e) {
        if (log.isErrorEnabled()) {
            log.error("Error occurred in subscriber: {}", s);
            log.error("Error occurred: {}", e.getMessage(), e);
        }
    }

    /**
     * 
     * @Description: BufferCleaner
     * @Author: Fred Feng
     * @Date: 29/12/2024
     * @Version 1.0.0
     */
    private class BufferCleaner extends TimerTask {

        private final Timer timer;

        BufferCleaner(long bufferCleanInterval) {
            timer = new Timer("BufferCleaner");
            timer.schedule(this, bufferCleanInterval, bufferCleanInterval);
        }

        public void stop() {
            if (timer != null) {
                timer.cancel();
            }
        }

        @Override
        public void run() {
            if (buffer.size() == 0) {
                return;
            }
            int lag = publisher.estimateMaximumLag();
            System.out.println("EventPublisher performance: " + lag + "\t"
                    + publisher.getMaxBufferCapacity() + "\t" + buffer.size());
            if (log.isTraceEnabled()) {
                log.trace("EventPublisher performance: {}/{}/{}", lag,
                        publisher.getMaxBufferCapacity(), buffer.size());
            }
            if (lag + requestFetchSize > publisher.getMaxBufferCapacity()) {
                return;
            }
            Collection<T> results;
            if (lag == 0) {
                results = buffer.poll(buffer.size());
            } else {
                results = buffer.poll(requestFetchSize);
            }

            if (CollectionUtils.isNotEmpty(results)) {
                results.forEach(event -> {
                    publish(event);
                });
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Executor executor = Executors.newFixedThreadPool(10);
        EventPublisher<Packet> eventPublisher =
                new EventPublisherImpl<>(executor, 256, 10, 100, new InMemoryBuffer<>(500), 5000);
        eventPublisher.subscribe(Arrays.asList(new EventSubscriber<Packet>() {

            @Override
            public void consume(Packet item) {
                System.out.println(ThreadUtils.currentThreadName() + " Received: " + item);
                ThreadUtils.randomSleep(500);
            }

            @Override
            public void handleError(Throwable e) {
                e.printStackTrace();
            }


        }));
        for (int i = 0; i < 1000; i++) {
            eventPublisher.publish(Packet.byString("Item_" + i));
        }
        System.in.read();
        eventPublisher.destroy();
        ExecutorUtils.gracefulShutdown(executor, 60000);
    }

}
