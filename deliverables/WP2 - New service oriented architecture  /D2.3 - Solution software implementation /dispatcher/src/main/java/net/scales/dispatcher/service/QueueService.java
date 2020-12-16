package net.scales.dispatcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.scales.dispatcher.model.Event;

/**
 * Manages a queue and executes a single async thread to process the queue whenever an item is added to the queue
 */
@Service
public class QueueService {

    private final static Logger logger = LoggerFactory.getLogger(QueueService.class);

    private static final BlockingQueue<Event> queue = new LinkedBlockingQueue<>();

    @Autowired
    private QueueProcessor queueProcessor;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        // Intitializes a single thread executor, this will ensure only one thread
        // processes the queue
        executorService = Executors.newSingleThreadExecutor();
    }

    public void add(Event data) {
        queue.add(data);

        logger.info("Added event data to queue, queue size: " + queue.size());

        // Calls executor service
        executorService.submit(queueProcessor);
    }

    public BlockingQueue<Event> getQueue() {
        return queue;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

}