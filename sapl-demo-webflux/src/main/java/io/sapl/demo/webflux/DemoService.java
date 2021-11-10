package io.sapl.demo.webflux;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import io.sapl.spring.method.metadata.EnforceDropWhileDenied;
import io.sapl.spring.method.metadata.EnforceRecoverableIfDenied;
import io.sapl.spring.method.metadata.EnforceTillDenied;
import io.sapl.spring.method.metadata.PostEnforce;
import io.sapl.spring.method.metadata.PreEnforce;
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
 * Also, there is no reason why different tiers of the application should not be
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
	 * handlers of the reactive stream, e.g., onSubscription, onNext, onError etc.
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
	 * only if the Publisher is of type Mono<?>. It cannot be combined with
	 * other @EnforceX annotations on the same method. Also, it cannot be combined
	 * with Spring security method security annotations, e.g., @PreAuthorize.
	 * 
	 * @return a protected String
	 */
	@PreEnforce
	public Mono<String> getMonoString() {
		return Mono.just("data returned by Mono");
	}

	/**
	 * The @PostEnforce annotation is typically used, if the return object of a
	 * protected method is required to make the decision, or if the return object
	 * has potentially to be modified via a transformation statement in a policy.
	 * 
	 * As an AuthorizationSubscription has to be constructed supplying the resource
	 * to be modified, and this value has to be well-defined, this annotation is
	 * only applicable to methods returning a Mono<>.
	 * 
	 * In this case, adding the SpEL expression {code resource="returnObject"} to the
	 * annotation has the effect to tell the PEP to set the return object of the
	 * Mono as the resource value of the AuthorizationSubscription to the PDP.
	 * 
	 * Please note, that in the AuthorizationSubscription the object has to be
	 * marshaled to JSON. For this to work one has to ensure, that the default
	 * Jackson ObjectMapper in the application context knows to do this for the
	 * given type. Thus, it may be necessary to deploy matching custom serializers
	 * or to annotate the class with the matching Jackson annotations.
	 * 
	 * @return a protected string
	 */
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
	 * handlers of the reactive stream, e.g., onSubscription, onNext, onError etc.
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
	 * on the same method. Also, it cannot be combined with Spring security method
	 * security annotations, e.g., @PreAuthorize.
	 * 
	 * @return a protected sequence of Integers, each delayed by 500ms.
	 */
	@PreEnforce
	public Flux<Integer> getFluxNumbers() {
		return Flux.concat(Flux.just(0), Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9).delayElements(Duration.ofMillis(500L)));
	}

	/**
	 * The @EnforceTillDenied annotation wraps the Flux<> in a PEP.
	 * 
	 * The access control only starts when a subscriber subscribes to the wrapped
	 * Flux<>, not at construction time of the Flux<>.
	 * 
	 * The basic concept of the @EnforceTillDenied PEP is to grant access to the
	 * FLux<> upon an initial PERMIT decision and to grant access until a non-PERMIT
	 * decision is received.
	 * 
	 * Upon the initial PERMIT, the PEP subscribes to the original Flux<>. During
	 * access to the Flux<>, all constraints are enforced.
	 * 
	 * Upon receiving a new PERMIT decision with different constraints, the
	 * constraint handling is updated accordingly.
	 * 
	 * Upon receiving a non-PERMIT decision, the final constraints are enforced, and
	 * an AccessDeniedException ends the Flux<>.
	 * 
	 * The @EnforceTillDenied annotation cannot be combined with any other
	 * enforcement annotation.
	 * 
	 * @return a protected sequence of messages, each delayed by 500ms.
	 */
	@EnforceTillDenied
	public Flux<String> getFluxString() {
		return Flux.just(
				"<-obligation will log different messages over time until access denied. Access is denied within the last 20 seconds of a local minute->)")
				.repeat().delayElements(Duration.ofMillis(500L));
	}

	/**
	 * The @EnforceDropWhileDenied annotation wraps the Flux<> in a PEP.
	 * 
	 * The access control only starts when a subscriber subscribes to the wrapped
	 * Flux<>, not at construction time of the Flux<>.
	 * 
	 * The basic concept of the @EnforceDropWhileDenied PEP is to grant access to
	 * the FLux<> upon an initial PERMIT decision and to grant access until the
	 * client cancels the subscription, or the original Flux<> completes. However,
	 * whenever a non-PERMIT decision is received, all messages are dropped from the
	 * Flux<> until a new PERMIT decision is received.
	 * 
	 * The subscriber will not be made aware of the fact that events are dropped
	 * from the stream.
	 * 
	 * Upon the initial PERMIT, the PEP subscribes to the original Flux<>. During
	 * access to the Flux<>, all constraints are enforced.
	 * 
	 * Upon receiving a new PERMIT decision with different constraints, the
	 * constraint handling is updated accordingly.
	 * 
	 * Upon receiving a non-PERMIT decision, the constraints are enforced, and
	 * messages are dropped without sending a AccessDeniedException downstream. The
	 * date resumes on receiving a new PERMIT decision.
	 * 
	 * The @EnforceDropWhileDenied annotation cannot be combined with any other
	 * enforcement annotation.
	 * 
	 * @return a protected sequence of messages, each delayed by 500ms.
	 */
	@EnforceDropWhileDenied
	public Flux<String> getFluxStringDroppable() {
		return Flux.just(
				"TIME: %s <-obligation will log different messages over time until access denied. Access is denied within the last 20 seconds of a local minute. During this time no events will be visible and data flow will resume on the start of the next minute.->)")
				.repeat().delayElements(Duration.ofMillis(500L)).map(message -> String.format(message, Instant.now()));
	}

	/**
	 * The @EnforceRecoverableIfDenied annotation wraps the Flux<> in a PEP.
	 * 
	 * The access control only starts when a subscriber subscribes to the wrapped
	 * Flux<>, not at construction time of the Flux<>.
	 * 
	 * The basic concept of the @EnforceRecoverableIfDenied PEP is to grant access
	 * to the FLux<> upon an initial PERMIT decision and to grant access until the
	 * client cancels the subscription, or the original Flux<> completes. However,
	 * whenever a non-PERMIT decision is received, all messages are dropped from the
	 * Flux<> until a new PERMIT decision is received.
	 * 
	 * The subscriber will be made not be made aware of the fact that events are
	 * dropped from the stream by sending AccessDeniedExceptions on a non-PERMIT
	 * decision.
	 * 
	 * The subscriber can then decide to stay subscribed via .onErrorContinue().
	 * Without .onErrorContinue this behaves similar to @EnforceTillDenied. With
	 * .onErrorContinue() this behaves similar to @EnforceDropWhileDenied however
	 * the subscriber can explicitly handle the event that access is denied and
	 * choose to stay subscribed or not.
	 * 
	 * The @EnforceRecoverableIfDenied annotation cannot be combined with any other
	 * enforcement annotation.
	 * 
	 * @return a protected sequence of messages, each delayed by 500ms.
	 */
	@EnforceRecoverableIfDenied
	public Flux<String> getFluxStringRecoverable() {
		return Flux.just(
				"TIME: %s <-obligation will log different messages over time until access denied. Access is denied within the last 20 seconds of a local minute. The DENY will be logged by the consumer of the service which is aware of the deny. During this time no events will be visible and data flow will resume on the start of the next minute.->)")
				.repeat().delayElements(Duration.ofMillis(500L)).map(message -> String.format(message, Instant.now()));
	}
}
