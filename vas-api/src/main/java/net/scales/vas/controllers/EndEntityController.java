package net.scales.vas.controllers;

import net.scales.vas.models.ErrorResponse;
import net.scales.vas.models.ResultResponse;
import net.scales.vas.models.EndEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class EndEntityController {

	private final static Logger logger = LoggerFactory.getLogger(EndEntityController.class);

	private final KeycloakSecurityContext securityContext;

	@Value("${keycloak.auth-server-url}")
	private String url;
	
	@Value("${keycloak.realm}")
	private String realm;

	@Value("${group.end-entity}")
	private String endEntityGroup;

	@Value("${role.hub}")
	private String hubRole;

	@Value("${role.admin}")
	private String adminRole;

	public EndEntityController(KeycloakSecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	@GetMapping("/endEntity/{endEntityId}")
	public Object getEndEntityById(@PathVariable("endEntityId") String endEntityId) {
		try {
			Keycloak keycloak = getKeycloak();

			UserResource resource = keycloak.realm(realm).users().get(endEntityId);
			
			List<GroupRepresentation> groups = resource.groups();

			boolean isEndEntityGroup = false;

			for(GroupRepresentation group : groups) {
				if (group.getName().equals(endEntityGroup)) {
					isEndEntityGroup = true;
					break;
				}
			}

			if (!isEndEntityGroup) {
				throw new Exception();
			}

			UserRepresentation user = resource.toRepresentation();

			List<String> attribute = user.getAttributes().get("vat");

			return new ResultResponse(new EndEntity(user.getId(), attribute.size() > 0 ? attribute.get(0) : "", user.getUsername()));

		} catch(Exception ex) {
			return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "EndEntity not found");
		}
	}

	@GetMapping("/{hubId}/endEntityList")
	public Object getEndEntityListByHubId(
		@PathVariable("hubId") String hubId,
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			if (!hasRole(Arrays.asList(new String[] {adminRole, hubRole}))) {
				return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Has no permission");
			}

			Keycloak keycloak = getKeycloak();

			List<EndEntity> endEntities = new ArrayList<>();

			UserRepresentation user = keycloak.realm(realm).users().get(hubId).toRepresentation();

			List<String> attribute = user.getAttributes().get("group-id");

			if (attribute.size() == 0) {
				return new ResultResponse(endEntities);
			}

			List<UserRepresentation> users = keycloak.realm(realm).groups().group(attribute.get(0)).members((page - 1) * pageSize, pageSize);

			for (UserRepresentation item : users) {
				attribute = item.getAttributes().get("vat");

				endEntities.add(new EndEntity(item.getId(), attribute.size() > 0 ? attribute.get(0) : "", item.getUsername()));
			}

			return new ResultResponse(endEntities);

		} catch(Exception ex) {
			return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Hub not found");
		}
	}

	private Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
					.serverUrl(url)
					.realm(realm)
					.authorization(securityContext.getTokenString())
					.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
					.build();
	}

	private boolean hasRole(List<String> permissions) {
		Set<String> roles = securityContext.getToken().getRealmAccess().getRoles();

		for(String role : roles) {
			if(permissions.contains(role)){
				return true;
			}
		}

		return false;
	}

}