package io.sapl.benchmark;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSuite {
	private List<PolicyGeneratorConfiguration> cases;
}
