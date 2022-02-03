package io.sapl.demo.webflux.classified;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.PreEnforce;
import reactor.core.publisher.Flux;

@Service
public class DocumentsService {

	private final static Document[] documents = {
			new Document(NatoSecurityClassification.NATO_UNCLASSIFIED, "Introduction",
					"This demo collection of documents is filtered based on the documents "
							+ "classification and the users clrearance level. "
							+ "As this is an unauthenticated service, the demo assumes a "
							+ "different clearance level for the user based on time (changes every few seconds). "
							+ "In particular the purpose of this demo endpoint is to "
							+ "demonstrate the filtering of Flux elements based on a constraint "
							+ "in the authorization decision."),
			new Document(NatoSecurityClassification.NATO_RESTRICTED, "Door Operator Manual",
					"Doors are operated using doorhandles and locks."),
			new Document(NatoSecurityClassification.NATO_CONFIDENTIAL, "Sum of 2 and 2",
					"Contrary to common belief, 2+2 is not 5. In fact it is 4."),
			new Document(NatoSecurityClassification.NATO_SECRET, "Tea Preparation Secrets",
					"When brewing green tea for board members, remember to cool the water down to 80°C before adding the tea leafes."),
			new Document(NatoSecurityClassification.COSMIC_TOP_SECRET, "Alien Visitor Factsheet",
					"The only extraterrestrial alien to ever have visited Earth appeared in 2018. His name was Bob and he really enjoyed "
							+ "Taylor Swift, cow-tipping, and long walks on the beach. He finally left Earth early 2019, because he thought breathing through "
							+ "a nose was disgusting.")
	};

	@PreEnforce(genericsType=Document.class)
	public Flux<Document> getDocuments() {
		return Flux.fromArray(documents);
	};

}
