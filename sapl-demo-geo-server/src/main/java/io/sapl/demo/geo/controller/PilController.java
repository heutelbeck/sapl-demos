package io.sapl.demo.geo.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PilController {

	private static final String UNABLE_TO_GENERATE_PIL = "Unable to generate PIL";

	private static final String RECURRENT = "recurrent";

	private static final String HTTP_OK = "200";

	private static final int MIN_PASSENGER = 166;

	@GetMapping(value = "/pil", produces = "application/json")
	@ResponseBody
	public String retrievePil(@RequestParam("dep") String dep,
			@RequestParam("dest") String dest, @RequestParam("fltNo") String fltNo,
			@RequestParam("date") String date,
			@RequestParam("classification") int classification,
			@RequestParam(value = "type", required = false) String type) {

		if (RECURRENT.equals(type)) {
			return HTTP_OK;
		}
		else {
			try {
				PilDataConstructor pil = new PilDataConstructor(classification, dep, dest,
						fltNo, date, MIN_PASSENGER);
				return pil.getData();
			}
			catch (IOException e) {
				LOGGER.error(e.getMessage());
				return UNABLE_TO_GENERATE_PIL;
			}
		}
	}

}
