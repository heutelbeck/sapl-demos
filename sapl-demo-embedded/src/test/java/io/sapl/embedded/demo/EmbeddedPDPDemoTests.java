package io.sapl.embedded.demo;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

import org.junit.jupiter.api.Assertions;

public class EmbeddedPDPDemoTests {

	@Test
	public void demoIsExecutableWithoutError() {
		EmbeddedPDPDemo.setUseTestRuns(true);
		var resultCode = Assertions.assertDoesNotThrow(() -> new CommandLine(new EmbeddedPDPDemo()).execute());
		EmbeddedPDPDemo.setUseTestRuns(false);
		Assertions.assertEquals(0, resultCode);
	}

}
