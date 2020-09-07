package net.scales.dispatcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.scales.dispatcher.model.Event;
import net.scales.dispatcher.model.SubscriptionEntity;
import net.scales.dispatcher.repository.SubscriptionRepository;
import net.scales.dispatcher.utils.HttpUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Callable task to process the queue
 */
@Service
public class QueueProcessor implements Callable<Object> {

    private final static Logger logger = LoggerFactory.getLogger(QueueProcessor.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    private SubscriptionRepository repository;

    @Override
    public Object call() throws Exception {
        BlockingQueue<Event> queue = queueService.getQueue();

        ObjectMapper mapper = new ObjectMapper();

        while (!queue.isEmpty()) {
            logger.info("Processing data, queue size: " + queue.size());

            // Removes item from queue
            Event data = queue.poll();

            // Converts item to json string
            String json = mapper.writeValueAsString(data);

            // Gets subscriptions by event
            List<SubscriptionEntity> subscriptions = repository.findByEvent(data.getType());

            for (SubscriptionEntity subscription : subscriptions) {
                notifyToClient(subscription, json);
            }
        }

        return null;
    }

    private void notifyToClient(SubscriptionEntity subscription, String event) {
		try {
			// Creates params
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("data", event));

			int status = HttpUtils.post(subscription.getUrl(), params, null);

			if (status != 200) {
				logger.info("Can't send notification to the client: " + subscription.getId() + "," + subscription.getUrl());

			} else {
				logger.info("Sent notification to the client: " + subscription.getId() + "," + subscription.getUrl());
			}

		} catch (Exception ex) {
			logger.info("Throw error when send notification to the client: " + subscription.getId() + "," + subscription.getUrl());
		}
	}

}