package io.sapl.demo.filterchain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RestService {

	@GetMapping("public")
	public String publicService() {
		return "public information";
	}

	@GetMapping("secret")
	public String secretService() {
		return "secret information";
	}

}
