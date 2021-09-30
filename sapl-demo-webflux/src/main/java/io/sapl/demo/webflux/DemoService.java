package io.sapl.demo.webflux;

import java.time.Duration;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.annotations.PostEnforce;
import io.sapl.spring.method.annotations.PreEnforce;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * In this demo the Policy Enforcement Points are established on the level of
 * the services. Deploying the PEPs on this level has the advantage that the
 * data is primarily domain-driven. On controller level, the data in the various
 * Publishers may be more HTTP/SSE driven which makes manipulation more
 * difficult. However, if obligations are to be applied on HTTP level, the PEPs
 * should be established on the controller.
 * 
 * Also there is no reason why different tiers of the application should not be
 * secured independently. This may even be required to achieve the protection
 * goals.
 * 
 * These annotations can also be used on repository interfaces. E.g., for the
 * reactive MongoDB drivers.
 */
@Service
public class DemoService {

	/**
	 * The @PreEnforce annotation wraps the Mono<String> returned by the method with
	 * a Policy Enforcement point.
	 * 
	 * The access control only starts when a subscriber subscribes to the wrapped
	 * Mono<String>, not at construction time of the Mono<String>.
	 * 
	 * Before allowing the subscriber to access the original Mono<String>, the PEP
	 * constructs an AuthorizationSubscription and sends it to the PDP deployed in
	 * the infrastructure. The PEP consumes exactly one decision and then cancels
	 * its subscription to the PDP.
	 * 
	 * If the decision contained constraints, i.e., advice or obligations, them the
	 * PEP hooks the execution of the constraint handling into the matching signal
	 * handlers of the reactive stream, e.g., onSubscription, onNext, onError etc..
	 * 
	 * This means, that constraints contained within the one decision made by the
	 * PDP are enforces continuously throughout the lifetime of the reactive stream.
	 * E.g., a constrained hooked into the onNext signal path will be triggered on
	 * every data item published on the stream.
	 * 
	 * If you want to be able to react on changing decisions throughout the lifetime
	 * of the reactive stream, consider using the @Enforce annotation instead.
	 * 
	 * The @PreEnforce annotation can be combined with a @PostEnforce annotation,
	 * only if the Publisher is of type Mono<?>. It cannot be combined with @Enforce
	 * on the same method. Also it cannot be combined with Spring security method
	 * security annotations, e.g., @PreAuthorize.
	 * 
	 * @return a protected String
	 */
	@PreEnforce
	public Mono<String> getMonoString() {
		return Mono.just("data returnded by Mono");
	}

//	@PreEnforce
	@PostEnforce(resource = "returnObject")
	public Mono<String> getMonoStringWithPreAndPost() {
		return Mono.just("I will be decorated with * on the left and right, because the policy said so");
	}

	/**
	 * The @PreEnforce annotation wraps the Flux<Integer> returned by the method
	 * with a Policy Enforcement point.
	 * 
	 * The access control only starts when a subscriber subscribes to the wrapped
	 * Flux<Integer>, not at construction time of the Flux<Integer>.
	 * 
	 * Before allowing the subscriber to access the original Flux<Integer>, the PEP
	 * constructs an AuthorizationSubscription and sends it to the PDP deployed in
	 * the infrastructure. The PEP consumes exactly one decision and then cancels
	 * its subscription to the PDP.
	 * 
	 * If the decision contained constraints, i.e., advice or obligations, them the
	 * PEP hooks the execution of the constraint handling into the matching signal
	 * handlers of the reactive stream, e.g., onSubscription, onNext, onError etc..
	 * 
	 * This means, that constraints contained within the one decision made by the
	 * PDP are enforces continuously throughout the lifetime of the reactive stream.
	 * E.g., a constrained hooked into the onNext signal path will be triggered on
	 * every data item published on the stream.
	 * 
	 * If you want to be able to react on changing decisions throughout the lifetime
	 * of the reactive stream, consider using the @Enforce annotation instead.
	 * 
	 * The @PreEnforce annotation can be combined with a @PostEnforce annotation,
	 * only if the Publisher is of type Mono<?>. It cannot be combined with @Enforce
	 * on the same method. Also it cannot be combined with Spring security method
	 * security annotations, e.g., @PreAuthorize.
	 * 
	 * @return a protected sequence of Integers, each delayed by 500ms.
	 */
	@PreEnforce
	public Flux<Integer> getFluxNumbers() {
		return Flux.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).delayElements(Duration.ofMillis(500L));
	}

}
