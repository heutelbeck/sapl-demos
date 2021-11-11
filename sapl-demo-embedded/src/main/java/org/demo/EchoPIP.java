package org.demo;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import reactor.core.publisher.Flux;

/**
 * A simple non-streaming PIP echoing its input. *
 */
@PolicyInformationPoint(name = "echo", description = "PIP echoing the input value")
public class EchoPIP {

	@Attribute(name = "echo")
	public Flux<Val> echo(@Text Val value) {
		return Flux.just(value);
	}

}
