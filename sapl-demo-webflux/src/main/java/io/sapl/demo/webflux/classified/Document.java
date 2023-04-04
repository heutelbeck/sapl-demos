package io.sapl.demo.webflux.classified;

public record Document(NatoSecurityClassification classification, String title, String contents) {
}