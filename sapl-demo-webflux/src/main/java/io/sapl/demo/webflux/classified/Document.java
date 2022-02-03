package io.sapl.demo.webflux.classified;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	NatoSecurityClassification classification;

	String title;
	String contents;
}
