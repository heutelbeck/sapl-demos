package io.sapl.demo.webflux.medical;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
	String name;
	String icd11Code;
	String diagnosis;
}
