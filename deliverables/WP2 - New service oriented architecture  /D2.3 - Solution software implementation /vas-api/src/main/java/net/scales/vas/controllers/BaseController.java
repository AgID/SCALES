package net.scales.vas.controllers;

import net.corda.core.messaging.CordaRPCOps;
import net.scales.vas.server.NodeRPCConnection;

import java.util.List;
import java.util.Set;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class BaseController {

	protected final CordaRPCOps proxy;

	protected final KeycloakSecurityContext securityContext;

	@Value("${keycloak.auth-server-url}")
	protected String authUrl;

	@Value("${keycloak.realm}")
	protected String realm;

	public BaseController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		this.proxy = rpc.proxy;
		this.securityContext = securityContext;
	}

	protected Keycloak getKeycloak() {
		return KeycloakBuilder
					.builder()
					.serverUrl(authUrl)
					.realm(realm)
					.authorization(securityContext.getTokenString())
					.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
					.build();
	}

	protected boolean hasRole(List<String> permissions) {
		Set<String> roles = securityContext.getToken().getRealmAccess().getRoles();

		for(String role : roles) {
			if(permissions.contains(role)){
				return true;
			}
		}

		return false;
	}

}