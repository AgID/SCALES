package net.scales.dispatcher.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.scales.dispatcher.models.ResultResponse;
import net.scales.dispatcher.services.QueueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Define your API endpoints here.
 */
@RestController
public class WebhookController {

	private final static Logger logger = LoggerFactory.getLogger(WebhookController.class);

	@Autowired
	private QueueService queueService;

	/**
	 * Method to receive webhook event notification
	 */
	@PostMapping("/webhook")
	public Object webhook(@RequestParam("data") String data) {
		logger.info("The new invoice was minted with data: " + data);

		queueService.add(data);

		return new ResultResponse("");
	}

}