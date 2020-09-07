package net.scales.dispatcher.controller;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.scales.dispatcher.model.Event;
import net.scales.dispatcher.model.ResponseError;
import net.scales.dispatcher.model.ResponseSuccess;
import net.scales.dispatcher.model.SubscriptionEntity;
import net.scales.dispatcher.repository.SubscriptionRepository;
import net.scales.dispatcher.service.QueueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Defines your API endpoints here.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController {

	private final static Logger logger = LoggerFactory.getLogger(WebhookController.class);

	@Autowired
	private QueueService queueService;

	@Autowired
    private SubscriptionRepository repository;

	@Value("${api.key}")
	private String apiKey;

	/**
	 * Method to receive webhook event notification
	 */
	@PostMapping
	public ResponseEntity<Object> webhook(
		@RequestHeader(name="Authorization") String token,
		@RequestParam("type") String type,
		@RequestParam("data") String data,
		@RequestParam("hubId") String hubId,
		@RequestParam("endEntityId") String endEntityId,
		@RequestParam("vasUrl") String vasUrl
	) {
		// Checks permission to access
		if (!token.equals("Bearer " + apiKey)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		logger.info("Received notification: " + vasUrl + "," + hubId + "," + endEntityId + "," + type + "," + data);

		queueService.add(new Event(type, data, hubId, endEntityId, vasUrl));

		return ResponseEntity.ok().build();
	}

	/**
	 * Subscribes to receive notification
	 */
	@PostMapping("/subscribe")
	public Object subscribe(@RequestParam String event, @RequestParam String url) {
		if (!ArrayUtils.contains(new String[] { "invoice", "order" }, event)) {
			return new ResponseError(HttpStatus.BAD_REQUEST, "Parameter 'event' must be values: invoice, order");
		}

		List<SubscriptionEntity> result = repository.findByEventAndUrl(event, url);

		if (result.size() > 0) {
			return new ResponseError(HttpStatus.BAD_REQUEST, "This url was registered");
		}

		SubscriptionEntity entity = repository.save(new SubscriptionEntity(event, url));

		return new ResponseSuccess(entity.getId());
	}

	@GetMapping("/unsubscribe/{id}")
	public Object unsubscribe(@PathVariable("id") Long id) {
		if (!repository.findById(id).isPresent()) {
			return new ResponseError(HttpStatus.NOT_FOUND, "Subscription id is not found");
		}

		repository.deleteById(id);

		return new ResponseSuccess("");
	}

	/**
	 * Gets subscription list
	 */
    @GetMapping("/subscriptionList")
	public Object getSubscriptionList(
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		Pageable pageable = PageRequest.of((page - 1) * pageSize, pageSize);

		List<SubscriptionEntity> result = repository.findAll(pageable).getContent();

		return new ResponseSuccess(result);
	}

}