package io.sapl.demo.webflux.medical;

public record Patient(String name, String icd11Code, String diagnosis) {
}
