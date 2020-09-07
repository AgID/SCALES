package net.scales.vas.ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.keycloak.KeycloakSecurityContext;

@Controller
public class HomeController implements ErrorController {

	@Autowired
	private KeycloakSecurityContext securityContext;

	private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	@GetMapping(path = "/vas")
	public String home(Model model) {
		model.addAttribute("id", securityContext.getToken().getSubject());
		model.addAttribute("token", securityContext.getTokenString());
		return "home";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) throws ServletException {
		request.logout();
		return "redirect:/vas";
	}

	@GetMapping("/error")
	public String error(HttpServletRequest request) throws ServletException {
		request.logout();
		return "redirect:/vas";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@GetMapping("/notification")
	public SseEmitter notify(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store");

		SseEmitter emitter = new SseEmitter(3600_000L);

		this.emitters.add(emitter);

		emitter.onCompletion(() -> this.emitters.remove(emitter));
		emitter.onTimeout(() -> this.emitters.remove(emitter));

		return emitter;
	}

	@EventListener
	public void onNewEvent(String data) {
		List<SseEmitter> deadEmitters = new ArrayList<>();

		this.emitters.forEach(emitter -> {
			try {
				emitter.send(data);

			} catch (Exception e) {
				deadEmitters.add(emitter);
			}
		});

		this.emitters.removeAll(deadEmitters);
	}

}