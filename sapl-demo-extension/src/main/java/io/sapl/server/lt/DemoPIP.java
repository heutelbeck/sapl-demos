/* The package is intentionally in the io.sapl.server namespace, because the PDP
 * servers are configured to auto scan these packages for Spring beans. 
 * By putting the class in this namespace, it makes sure the PDP discovers it.
 */
package io.sapl.server.lt;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Text;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This is a small custom Policy Information Point for illustrating how to
 * implement such PIPs.
 */
/*
 * The @Component annotation makes sure, that the PIP is created in the Spring
 * application context when the component scan is performed.
 */
@Component
/*
 * The @PolicyInformationPoint is used by the PDP to identify the Beans, which
 * to import. The annotation is also used when manually instantiating a PDP
 * infrastructure. The 'name' field is optional. If left empty, the name will be
 * the class name. The name determines how the PIP can be addressed in policies
 * using the angled bracket notation. In this case the attributes here can be
 * accessed via '<demo.NAME_OF_ATTRIBUTE>'.
 * 
 * The 'description' field can be used to add some documentation. This is used
 * to automatically generate documentation pages in the PDP servers with a
 * graphical front-end. It has no impact on the evaluation of policies at
 * runtime.
 */
@PolicyInformationPoint(name = "demo", description = "Some documenting text for the PIP.")
public class DemoPIP {

	@Attribute(name = "toggle", docs = "Periodically turns from true to false.")
	public Flux<Val> toggle(Val leftHand, Map<String, JsonNode> variables) {
		return Flux.just(true, false).repeat().delayElements(Duration.ofMillis(500)).map(Val::of);
	}

	/**
	 * This method implements the attribute
	 * "host.name".<demo.reachable(pollingInterval,timeout)>.
	 * 
	 * The annotation @Attribute makes the PDP register the attribute. The name
	 * parameter can be used to set the name. If not set, the method name is used.
	 * The docs parameter is used to add documentation to the PDP. This is used to
	 * automatically generate documentation pages in the PDP servers with a
	 * graphical front-end. It has no impact on the evaluation of policies at
	 * runtime.
	 * 
	 * This attribute checks if a host is currently reachable within the network.
	 * 
	 * Each attribute can have a left hand input value and additional parameters.
	 * 
	 * The left hand input is what is noted left of the angled brackets within a
	 * policy. This left hand attribute is the entity of which this method returns
	 * an attribute. In this case reachability is an attribute of the host described
	 * by the host name.
	 * 
	 * The parameters in parentheses can be used to parameterize the attribute
	 * lookup. In this case, it defines the timeout implying the host to be not
	 * reachable and the interval in which the attribute should be calculated and
	 * returned.
	 * 
	 * Note, that these parameters are fluxes. This is due to the way SAPL can nest
	 * attribute lookups.
	 * 
	 * @param leftHandHostnameParameter a textual Val containing a host name, either
	 *                                  in number notation (e.g. "192.168.1.2") or
	 *                                  as domain name (e.g. "example.com").
	 * @param variables                 this map contains the current variables
	 *                                  available in the current policy evaluation
	 *                                  context. This attribute finder does not make
	 *                                  use of the variables.
	 * @param pollingIntervalParameter  a numeric Val providing the interval in
	 *                                  milliseconds in which host is probed. Must
	 *                                  be larger than timeoutMsParameter.
	 * @param timeoutMsParameter        a numeric Val providing the timeout in
	 *                                  milliseconds where if the host fails to
	 *                                  reply within this time the host is
	 *                                  considered to be not reachable. Must me
	 *                                  smaller than timeoutMsParameter.
	 * @return A boolean Flux indication the hosts availability.
	 */
	@Attribute(name = "reachable", docs = "Checks if the internet address is reachable within a given timout. Usage: \"example.com\".<demo.reachable(5000,6000)> checks if the address returns a package within 5000ms and repeats this pingin action every 6000ms. The timeout must be smaller than the repetition interval.")
	public Flux<Val> reachable(@Text Val leftHandHostnameParameter, Map<String, JsonNode> variables,
			Flux<Val> pollingIntervalParameter, Flux<Val> timeoutMsParameter) {
		return Flux.combineLatest(values -> {
			return new Val[] { (Val) values[0], (Val) values[1] };
		}, timeoutMsParameter, pollingIntervalParameter).flatMap(parameters -> {
			var hostname = leftHandHostnameParameter.getText();
			var timeoutMs = parameters[0].get().asInt();
			var pollingIntervalMs = parameters[1].get().asLong();
			if (pollingIntervalMs < timeoutMs)
				return Flux.error(new PolicyEvaluationException(
						"When checking for reachablility of a host, the timeout must be smaller than the polling interval. The timout was %dms and the polling intervall was set to %dms",
						timeoutMs, pollingIntervalMs));
			return reachable(hostname, pollingIntervalMs, timeoutMs).map(Val::of);
		});
	}

	/**
	 * This function first resolves the host name and then repeatedly checks
	 * availability of the host. This method only emits an event if the availability
	 * status changes.
	 * 
	 * @param hostname          the host name to resolve
	 * @param pollingIntervalMs the polling time in ms
	 * @param timeout           the timeout in ms
	 * @return A boolean Flux indication the hosts availability.
	 */
	private Flux<Boolean> reachable(String hostname, long pollingIntervalMs, int timeout) {
		return dnsLookup(hostname).repeat().delayElements(Duration.ofMillis(pollingIntervalMs))
				.flatMap(reachable(timeout)).distinctUntilChanged();
	}

	/**
	 * Wraps the blocking getByName() method of InetAddress in a Mono.
	 * 
	 * Usually it is not a good idea to use blocking APIs. Sometimes there are no
	 * asynchronous implementations available and one has no resources to implement
	 * an asynchronous replacement. There generally are asynchronous libraries
	 * available. However, as this is just a tutorial project we omit additional
	 * dependencies for simplicity sake.
	 * 
	 * @param hostname the hostname to resolve.
	 * @return the resolved hostname
	 */
	private Mono<InetAddress> dnsLookup(String hostname) {
		return Mono.fromCallable(() -> InetAddress.getByName(hostname));
	}

	/**
	 * Wraps the blocking isReachable() method of InetAddress in a Mono.
	 * 
	 * Usually it is not a good idea to use blocking APIs. Sometimes there are no
	 * asynchronous implementations available and one has no resources to implement
	 * an asynchronous replacement. There generally are asynchronous libraries
	 * available. However, as this is just a tutorial project we omit additional
	 * dependencies for simplicity sake.
	 * 
	 * @param timeout
	 * @return true if the host replied in time
	 */
	private Function<InetAddress, Mono<Boolean>> reachable(int timeout) {
		return inetAddress -> Mono.fromCallable(() -> {
			try {
				return inetAddress.isReachable(timeout);
			} catch (IOException e) {
				return false;
			}
		});
	}

}
