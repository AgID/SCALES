package net.scales.dispatcher.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * Callable task to process the queue
 */
@Service
public class QueueProcessor implements Callable<Object> {

    private final static Logger logger = LoggerFactory.getLogger(QueueProcessor.class);

    @Autowired
    private QueueService queueService;

    @Value("${vas.ui.url}")
	private String webhookUrl;

    @Override
    public Object call() throws Exception {
        BlockingQueue<String> queue = queueService.getQueue();

        while (!queue.isEmpty()) {
            // Remove item from queue
            String data = queue.poll();

            notifyToVasUI(data);

            logger.info("Processing data, queue size: " + queue.size());
        }

        return null;
    }

    private void notifyToVasUI(String data) throws Exception {
		// Create params
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", data));

		HttpPost request = new HttpPost(webhookUrl);
		request.setEntity(new UrlEncodedFormEntity(params));

		// Make request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(request);

		if (response.getStatusLine().getStatusCode() != 200) {
			logger.error("", "Can not notify to the vas ui for data " + data);
		}
	}

}