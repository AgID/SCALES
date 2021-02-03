package net.scales.vas.controllers;

import net.scales.vas.models.ResponseError;
import net.scales.vas.models.ResponseSuccess;
import net.scales.vas.server.NodeRPCConnection;
import net.scales.vas.models.EndEntity;

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
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * Defines your API endpoints here.
 */
@RestController
public class EndEntityController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(EndEntityController.class);

	@Value("${group.end-entity}")
	private String endEntityGroup;

	@Value("${role.hub}")
	private String hubRole;

	@Value("${role.admin}")
	private String adminRole;

	public EndEntityController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		super(rpc, securityContext);
	}

	@GetMapping("/endEntity/{endEntityId}")
	public Object getEndEntityById(@PathVariable("endEntityId") String endEntityId) {
		try {
			// Gets "ee" account by id
			UserResource resource = getKeycloak().realm(realm).users().get(endEntityId);

			boolean isEndEntityGroup = false;

			for (GroupRepresentation group : resource.groups()) {
				if (group.getName().equals(endEntityGroup)) {
					isEndEntityGroup = true;
					break;
				}
			}

			// Checks if account belongs to "ee" group
			if (!isEndEntityGroup) {
				throw new Exception();
			}

			UserRepresentation user = resource.toRepresentation();

			return new ResponseSuccess(createEndEntity(user.getAttributes(), user));

		} catch(Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			return new ResponseError(HttpStatus.NOT_FOUND, "End Entity is not found");
		}
	}

	@GetMapping("/{hubId}/endEntityList")
	public Object getEndEntityListByHubId(
		@PathVariable("hubId") String hubId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			// Checks if has permission to view information of "ee" accounts
			if (!hasRole(Arrays.asList(new String[] { adminRole, hubRole }))) {
				return new ResponseError(HttpStatus.FORBIDDEN, "Has no permission");
			}

			Keycloak keycloak = getKeycloak();

			List<EndEntity> endEntities = new ArrayList<>();

			// Gets "hub" account by id
			UserRepresentation user = keycloak.realm(realm).users().get(hubId).toRepresentation();

			// Gets attributes of "hub" account
			List<String> attribute = user.getAttributes().get("group-id");

			// Returns empty list if has no attribute
			if (attribute.size() == 0) {
				return new ResponseSuccess(endEntities);
			}

			// Gets "ee" accounts by "hub"
			List<UserRepresentation> users = keycloak.realm(realm).groups().group(attribute.get(0)).members((page - 1) * pageSize, pageSize);

			for (UserRepresentation item : users) {
				endEntities.add(createEndEntity(item.getAttributes(), item));
			}

			return new ResponseSuccess(endEntities);

		} catch(Exception ex) {
			if (ex instanceof NotAuthorizedException) {
				return new ResponseError(HttpStatus.UNAUTHORIZED);
			}

			return new ResponseError(HttpStatus.NOT_FOUND, "Hub is not found");
		}
	}

	private EndEntity createEndEntity(Map<String, List<String>> attributes, UserRepresentation user) {
		return new EndEntity(
			user.getId(),
			user.getUsername(),
			attributes.get("name").size() > 0 ? attributes.get("name").get(0) : "",
			attributes.get("description").size() > 0 ? attributes.get("description").get(0) : "",
			attributes.get("fiscal-code").size() > 0 ? attributes.get("fiscal-code").get(0) : ""
		);
	}

}