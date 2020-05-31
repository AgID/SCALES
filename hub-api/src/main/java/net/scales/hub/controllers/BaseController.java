package net.scales.hub.controllers;

import net.corda.core.messaging.CordaRPCOps;
import net.scales.hub.server.NodeRPCConnection;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api")
public class BaseController {

	protected final CordaRPCOps proxy;

	private final KeycloakSecurityContext securityContext;

	@Value("${keycloak.auth-server-url}")
	private String authUrl;

	@Value("${keycloak.realm}")
	private String realm;

	public BaseController(NodeRPCConnection rpc, KeycloakSecurityContext securityContext) {
		this.proxy = rpc.proxy;
		this.securityContext = securityContext;
	}

	protected Keycloak getKeycloak() {
        return KeycloakBuilder.builder()
					.serverUrl(authUrl)
					.realm(realm)
					.authorization(securityContext.getTokenString())
					.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
					.build();
	}

	protected AccessToken getAccessToken() {
        return securityContext.getToken();
	}

	protected String getRealm() {
        return realm;
	}

}