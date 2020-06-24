package net.scales.vas.controllers;

import net.scales.vas.models.ErrorResponse;
import net.scales.vas.models.Hub;
import net.scales.vas.models.ResultResponse;

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
public class HubController {

	private final static Logger logger = LoggerFactory.getLogger(HubController.class);

	private final KeycloakSecurityContext securityContext;

	@Value("${keycloak.auth-server-url}")
	private String url;
	
	@Value("${keycloak.realm}")
	private String realm;

	@Value("${group.hub}")
	private String hubGroup;

	@Value("${group.hub-id}")
	private String hubGroupId;

	@Value("${role.hub}")
	private String hubRole;

	@Value("${role.admin}")
	private String adminRole;

	public HubController(KeycloakSecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	@GetMapping("/hub/{hubId}")
	public Object getHubById(@PathVariable("hubId") String hubId) {
		try {
			if (!hasRole(Arrays.asList(new String[] {adminRole, hubRole}))) {
				return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Has no permission");
			}

			Keycloak keycloak = getKeycloak();

			UserResource resource = keycloak.realm(realm).users().get(hubId);

			List<GroupRepresentation> groups = resource.groups();

			boolean isHubGroup = false;

			for(GroupRepresentation group : groups) {
				if (group.getName().equals(hubGroup)) {
					isHubGroup = true;
					break;
				}
			}

			if (!isHubGroup) {
				throw new Exception();
			}

			UserRepresentation user = resource.toRepresentation();

			List<String> attribute = user.getAttributes().get("vat");

			return new ResultResponse(new Hub(user.getId(), attribute.size() > 0 ? attribute.get(0) : "", user.getUsername()));

		} catch(Exception ex) {
			return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Hub not found");
		}
	}

	@GetMapping("/hubList")
	public Object getHubList(
		@RequestParam(name="page", defaultValue="1") int page,
		@RequestParam(name="pageSize", defaultValue="10") int pageSize
	) {
		try {
			if (!hasRole(Arrays.asList(new String[] {adminRole}))) {
				return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Has no permission");
			}

			Keycloak keycloak = getKeycloak();

			List<UserRepresentation> users = keycloak.realm(realm).groups().group(hubGroupId).members((page - 1) * pageSize, pageSize);

			List<Hub> hubs = new ArrayList<>();

			for (UserRepresentation user : users) {
				List<String> attribute = user.getAttributes().get("vat");

				hubs.add(new Hub(user.getId(), attribute.size() > 0 ? attribute.get(0) : "", user.getUsername()));
			}

			return new ResultResponse(hubs);

		} catch(Exception ex) {
			logger.error("", ex);

			return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
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