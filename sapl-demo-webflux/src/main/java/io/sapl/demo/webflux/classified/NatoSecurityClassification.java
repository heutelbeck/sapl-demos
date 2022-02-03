package io.sapl.demo.webflux.classified;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NatoSecurityClassification {
	NATO_UNCLASSIFIED("NATO UNCLASSIFIED", "NU"),
	NATO_RESTRICTED("NATO RESTRICTED", "NR"),
	NATO_CONFIDENTIAL("NATO CONFIDENTIAL", "NC"),
	NATO_SECRET("NATO SECRET", "NS"),
	COSMIC_TOP_SECRET("COSMIC TOP SECRET", "CTS");

	private String name;
	private String abbreviation;

	public String toString() {
		return name + " (" + abbreviation + ')';
	}
}
