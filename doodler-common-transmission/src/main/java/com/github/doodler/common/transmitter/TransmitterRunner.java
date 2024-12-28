package com.github.doodler.common.transmitter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;
import com.github.doodler.common.utils.ExecutorUtils;
import com.github.doodler.common.utils.LangUtils;
import com.github.doodler.common.utils.MapUtils;
import com.github.doodler.common.utils.ThreadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description: TransmitterRunner
 * @Author: Fred Feng
 * @Date: 28/12/2024
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TransmitterRunner implements Runnable {

    private final String collectionName;
    private final TransmitterBufferProperties bufferProperties;
    private final BufferArea bufferArea;
    private final TaskExecutor threadPool;

    private final Map<String, List<Handler>> topicTransmitters =
            new ConcurrentHashMap<String, List<Handler>>();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread runner;

    public void configure(List<Handler> transmitters) {
        if (CollectionUtils.isNotEmpty(transmitters)) {
            transmitters.forEach(this::addTransmitter);
        }
    }

    public void addTransmitter(Handler transmitter) {
        Assert.notNull(transmitter, "NonNull transmitter");
        List<Handler> transmitters = MapUtils.getOrCreate(topicTransmitters,
                transmitter.getTopic(), () -> new CopyOnWriteArrayList<Handler>());
        transmitters.add(transmitter);
        log.info("Add Transmitter: {}/{}", transmitter.getTopic(), transmitter);
    }

    public void removeTransmitter(Handler transmitter) {
        Assert.notNull(transmitter, "NonNull transmitter");
        List<Handler> transmitters = topicTransmitters.get(transmitter.getTopic());
        if (transmitters != null) {
            while (transmitters.contains(transmitter)) {
                transmitters.remove(transmitter);
            }
            log.info("Remove Transmitter: {}/{}", transmitter.getTopic(), transmitter);
        }
    }

    public int countOfTransmitter() {
        return topicTransmitters.size();
    }

    public int countOfTransmitter(String topic) {
        return topicTransmitters.containsKey(topic) ? topicTransmitters.get(topic).size() : 0;
    }

    public void startDaemon() {
        if (isStarted()) {
            throw new TransmitterException("TransmitterRunner is started.");
        }
        running.set(true);
        runner = ThreadUtils.runAsThread(this);
        log.info("TransmitterRunner is started.");

    }

    public boolean isStarted() {
        return running.get();
    }

    public void stop() {
        if (!isStarted()) {
            throw new TransmitterException("TransmitterRunner is not started.");
        }
        running.set(false);
        if (runner != null) {
            try {
                runner.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("TransmitterRunner is stoped.");
        }
    }

    @Override
    public void run() {
        log.info("Start Loop {}...", collectionName);
        while (running.get()) {
            Collection<Packet> packets = null;
            try {
                packets = bufferArea.poll(
                        LangUtils.coalesce(collectionName,
                                bufferProperties.getDefaultCollectionName()),
                        bufferProperties.getBatchSize());
            } catch (Throwable e) {
                if (log.isTraceEnabled()) {
                    log.trace(e.getMessage(), e);
                }
            }
            if (CollectionUtils.isNotEmpty(packets)) {
                if (topicTransmitters.size() > 0) {
                    for (Packet packet : packets) {
                        List<Handler> transmitters = topicTransmitters.get(packet.getTopic());
                        if (CollectionUtils.isNotEmpty(transmitters)) {
                            for (Handler transmitter : transmitters) {
                                Packet copy = packet.copy();
                                ExecutorUtils.runInBackground(threadPool, () -> {
                                    transmitter.process(copy);
                                });
                            }
                        }
                    }
                }
                packets = null;
            } else {
                ThreadUtils.randomSleep(1000L);
            }
        }
        log.info("Ending Loop {}!", collectionName);
    }

}
