package net.scales.vas.controllers;

import net.scales.vas.models.ResponseError;
import net.scales.vas.models.Hub;
import net.scales.vas.models.ResponseSuccess;
import net.scales.vas.server.NodeRPCConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * Defines your API endpoints here.
 */
@RestController
public class HubController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(HubController.class);

	@Value("${group.hub}")
	private String hubGroup;

	@Value("${group.hub-id}")
	private String hubGroupId;

	@Value("${role.hub}")
	private String hubRole;

	@Value("${role.admin}")
	private String adminRole;

	public HubController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@GetMapping("/hub/{hubId}")
	public Object getHubById(@PathVariable("hubId") String hubId) {
		try {
			// Checks if has permission to view information of "hub" account
			if (!hasRole(Arrays.asList(new String[] { adminRole, hubRole }))) {
				return new ResponseError(HttpStatus.FORBIDDEN, "Has no permission");
			}

			// Gets "hub" account by id
			UserResource resource = getKeycloak().realm(realm).users().get(hubId);

			boolean isHubGroup = false;

			for (GroupRepresentation group : resource.groups()) {
				if (group.getName().equals(hubGroup)) {
					isHubGroup = true;
					break;
				}
			}

			// Checks if account belongs to "hub" group
			if (!isHubGroup) {
				throw new Exception();
			}

			UserRepresentation user = resource.toRepresentation();

			return new ResponseSuccess(createHub(user));

		} catch(Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			return new ResponseError(HttpStatus.NOT_FOUND, "Hub is not found");
		}
	}

	@GetMapping("/hubList")
	public Object getHubList(
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			// Checks if has permission to view information of "hub" accounts
			if (!hasRole(Arrays.asList(new String[] { adminRole }))) {
				return new ResponseError(HttpStatus.FORBIDDEN, "Has no permission");
			}

			// Gets "hub" accounts by group
			List<UserRepresentation> users = getKeycloak().realm(realm).groups().group(hubGroupId).members((page - 1) * pageSize, pageSize);

			List<Hub> hubs = new ArrayList<>();

			for (UserRepresentation user : users) {
				hubs.add(createHub(user));
			}

			return new ResponseSuccess(hubs);

		} catch(Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			logger.error("", ex);

			return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Hub createHub(UserRepresentation user) {
		return new Hub(
			user.getId(),
			user.getUsername()
		);
	}

}