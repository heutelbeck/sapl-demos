package io.sapl.demo.jwt.resourceserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sapl.spring.method.metadata.PreEnforce;

@RestController
public class MiskatonicUniversityController {

	@GetMapping("/books")
	@PreEnforce(action = "'read'", resource = "'books'")
	public String[] books() {
		// @formatter:off
		return new String[] {	"Necronomicon", 
								"Nameless Cults", 
								"Book of Eibon"	};
		// @formatter:on
	}

	@GetMapping("/faculty")
	@PreEnforce(action = "'read'", resource = "'faculty'")
	public String[] getMessages() {
		// @formatter:off
		return new String[] { 	"Dr. Henry Armitage", 
								"Professor Ferdinand C. Ashley", 
								"Professor Atwood",
								"Professor Dexter", 
								"Professor William Dyer", 
								"Professor Ellery", 
								"Professor Tyler M. Freeborn",
								"Dr. Allen Halsey",
								"Professor Lake",
								"Dr. Francis Morgan",
								"Professor Frank H. Pabodie",
								"Professor Nathaniel Wingate Peaslee",
								"Professor Wingate Peaslee",
								"Professor Warren Rice",
								"Professor Upham",
								"\"Old\" Waldron",
								"Albert N. Wilmarth" };
		// @formatter:on
	}

	@GetMapping("/bestiary")
	@PreEnforce(action = "'read'", resource = "'bestiary'")
	public String[] getBestiary() {
		// @formatter:off
		return new String[] { 	"Byakhee",
								"Deep Ones",
								"Elder Things",
								"Ghouls",
								"Gugs",
								"Hounds of Tindalos",
								"Men of Leng",
								"Mi-Go",
								"Moon-beasts",
								"Nightgaunts",
								"Serpent Men",
								"Shoggoths" };
		// @formatter:on
	}

}
