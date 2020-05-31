package net.scales.vas.ui.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.scales.vas.ui.models.ResultResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Define your API endpoints here.
 */
@RestController
public class WebhookController {

	private final static Logger logger = LoggerFactory.getLogger(WebhookController.class);

	private final ApplicationEventPublisher eventPublisher;

	public WebhookController(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Method to receive webhook event notification
	 */
	@PostMapping("/webhook")
	public Object webhook(@RequestParam("data") String data) {
		logger.info("The new invoice was minted with data: " + data);

		eventPublisher.publishEvent(data);

		return new ResultResponse("");
	}

}