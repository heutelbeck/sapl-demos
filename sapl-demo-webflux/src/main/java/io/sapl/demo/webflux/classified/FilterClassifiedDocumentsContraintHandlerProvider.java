package io.sapl.demo.webflux.classified;

import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.spring.constraints.api.FilterPredicateConstraintHandlerProvider;

@Service
public class FilterClassifiedDocumentsContraintHandlerProvider
		implements FilterPredicateConstraintHandlerProvider<Document> {

	@Override
	public boolean isResponsible(JsonNode constraint) {
		return constraint != null && constraint.has("type")
				&& "filterClassifiedDocuments".equals(constraint.findValue("type").asText());
	}

	@Override
	public Class<Document> getSupportedType() {
		return Document.class;
	}

	@Override
	public Predicate<Document> getHandler(JsonNode constraint) {
		var clearanceAux = NatoSecurityClassification.NATO_UNCLASSIFIED;

		if (constraint.has("clearance")) {
			try {
				clearanceAux = NatoSecurityClassification.valueOf(constraint.findValue("clearance").asText());
			} catch (IllegalArgumentException e) {
				; // NOOP
			}
		}

		var clearance = clearanceAux;

		return document -> clearanceMatchesOrIsHigherThanClassification(clearance, document.getClassification());
	}

	private boolean clearanceMatchesOrIsHigherThanClassification(
			NatoSecurityClassification clearance,
			NatoSecurityClassification classification) {
		return classification.compareTo(clearance) <= 0;
	}

}
