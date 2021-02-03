package net.scales.vas.ui.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Defines your API endpoints here.
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
	public ResponseEntity<Object> webhook(@RequestParam("data") String data) {
		logger.info("Received notification: " + data);

		eventPublisher.publishEvent(data);

		return ResponseEntity.ok().build();
	}

}