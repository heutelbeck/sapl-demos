package io.sapl.demo.axon.iface.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
public class RedirectController {

	@GetMapping("/")
	public RedirectView redirectToApi(RedirectAttributes attributes) {
		return new RedirectView("/swagger-ui/index.html");
	}
}