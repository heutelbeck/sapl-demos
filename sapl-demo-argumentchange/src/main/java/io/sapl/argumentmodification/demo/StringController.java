package io.sapl.argumentmodification.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class StringController {

	private final StringService service;

	@GetMapping(value = "/string", produces = MediaType.APPLICATION_JSON_VALUE)
	public String someString() {
		var resultString = service.lowercase("IF ALL TEXT IS LOWERCASE THE SERVICE WAS CALLED."
				+ " RIGHT TO THIS MESSAGE THERE IS A LOWERCASE 'HELLO MODIFICATION' THEN THE OBLICATION SUCCESSFULLY MODIFIED THE METHOD ARGUMENTS->");
		return resultString;
	}
}
