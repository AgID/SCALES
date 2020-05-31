package net.scales.dispatcher.services;

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

/**
 * Manages a queue and executes a single async thread to process the queue whenever an item is added to the queue
 */
@Service
public class QueueService {

    private final static Logger logger = LoggerFactory.getLogger(QueueService.class);

    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Autowired
    private QueueProcessor queueProcessor;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        // Intitialize a single thread executor, this will ensure only one thread
        // processes the queue
        executorService = Executors.newSingleThreadExecutor();
    }

    public void add(String data) {
        queue.add(data);

        logger.info("Added data to queue, queue size: " + queue.size());

        // Call executor service
        executorService.submit(queueProcessor);
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

}