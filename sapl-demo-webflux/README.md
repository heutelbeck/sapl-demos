# Demo - Reactive Policy Enforcement Points in Webflux

This demo shows how to add reactive policy enforcement points to components in a Webflux-based API.

While in non-reactive environments only the two annotations `@PreEnforce` and `@PostEnforce` for 
method invocation interception.

In a Webflux environment with methods returning reactive types, i.e., `Flux<?>` and `Mono<?>` these
annotations get a different behavior and some additional annotations are available.

The demo can be started by executing `mvn spring-boot:run` in its modules root directory.

The demo will expose REST and Server Sent Events (SSE) end points, which connect to a 
service bean which has matching methods secured by SAPL annotations.

The different end points can be accessed as follows:

* <http://localhost:8080/numbers>: Sends a sequence of numbers as SSE. For each number two 
  constraint handlers are triggered, logging messages to the servers console. 
  The matching service is secured with `@PreEnforce`.

* <http://localhost:8080/string>: Sends a single String. Two constraint handlers are triggered, 
  logging messages to the servers console. 
  The matching service is secured with `@PreEnforce`.
  
* <http://localhost:8080/changedstring>: Returns a single string. The string is changed by the 
  decision containing a resource generated in a `transform` statement in the matching policy.
  The matching service is secured with `@PostEnforce`.

* <http://localhost:8080/enforcetilldeny>: Returns a sequence of strings. The matching policy 
  is time-based single string and access depends on the current second of the current minute 
  of the system clock. For the first 40 seconds access is granted then access is denied. 
  If you directly get an `ACCESS DENIED` message try reloading the endpoint when the local 
  minute rolls over. Different messages are logged by different constraints during the first 
  and the second 20 seconds of the minute.
  The matching service is secured with `@EnforceTillDenied`.

* <http://localhost:8080/enforcedropwhiledeny>: Returns a sequence of strings. The matching policy 
  is time-based single string and access depends on the current second of the current minute 
  of the system clock. For the first 40 seconds access is granted then access is denied. 
  If you get no message, wait till the local minute rolls over. 
  Different messages are logged by different constraints during the first and the second 20 seconds 
  of the minute.
  The matching service is secured with `@EnforceDropWhileDenied`.

* <http://localhost:8080/enforcerecoverableifdeny>: Returns a sequence of strings. The matching policy 
  is time-based single string and access depends on the current second of the current minute 
  of the system clock. For the first 40 seconds access is granted then access is denied. 
   If you directly get an `ACCESS DENIED` message, wait till the local minute rolls over. 
  Different messages are logged by different constraints during the first and the second 20 seconds 
  of the minute.
  The matching service is secured with `@EnforceRecoverableIfDenied`.

## `@PreEnforce`

The `@PreEnforce` annotation wraps the `Mono<>` or `Flux<>` returned by the method with
a Policy Enforcement point.

The access control only starts when a subscriber subscribes to the wrapped `Publisher<>`,
not at construction time of the `Publisher<>` object.
 
Before allowing the subscriber to access the original `Publisher<>`, the PEP
constructs an AuthorizationSubscription and sends it to the PDP deployed in
the infrastructure. The PEP consumes exactly one decision and then cancels
its subscription to the PDP.

If the decision contained constraints, i.e., advice or obligations, them the
PEP hooks the execution of the constraint handling into the matching signal
handlers of the reactive stream, e.g., onSubscription, onNext, onError etc.

This means, that constraints contained within the one decision made by the
PDP are enforces continuously throughout the lifetime of the reactive stream.
E.g., a constrained hooked into the onNext signal path will be triggered on
every data item published on the stream.

If you want to be able to react on changing decisions throughout the lifetime
of the reactive stream, consider using the @Enforce annotation instead.

The `@PreEnforce` annotation can be combined with a @PostEnforce annotation,
only if the Publisher is of type `Mono<?>`. It cannot be combined with
other `@EnforceX` annotations on the same method. Also it cannot be combined
with Spring security method security annotations, e.g., `@PreAuthorize`.

# `@PostEnforce`

The `@PostEnforce` annotation is typically used, if the return object of a
protected method is required to make the decision, or if the return object
has potentially to be modified via a transformation statement in a policy.
 
As an AuthorizationSubscription has to be constructed supplying the resource
to be modified, and this value has to be well-defined, this annotation is
only applicable to methods returning a `Mono<>`.

By adding the SpEL expression `resource="returnObject"` to the
annotation has the effect to tell the PEP to set the return object of the
Mono as the resource value of the AuthorizationSubscription to the PDP.

Please note, that in the AuthorizationSubscription the object has to be
marshaled to JSON. For this to work one has to ensure, that the default
Jackson ObjectMapper in the application context knows to to do this for the
given type. Thus, it may be necessary to deploy matching custom serializers
or to annotate the class with the matching Jackson annotations.


# `@EnforceTillDenied`

The `@EnforceTillDenied` annotation wraps the `Flux<>` in a PEP.

The access control only starts when a subscriber subscribes to the wrapped
`Flux<>`, not at construction time of the `Flux<>`.

The basic concept of the `@EnforceTillDenied` PEP is to grant access to the
FLux<> upon an initial `PERMIT` decision and to grant access until a non-`PERMIT`
decision is received.

Upon the initial `PERMIT`, the PEP subscribes to the original Flux<>. During
access to the `Flux<>`, all constraints are enforced.

Upon receiving a new `PERMIT` decision with different constraints, the
constraint handling is updated accordingly.

Upon receiving a non-`PERMIT` decision, the final constraints are enforced, and
an AccessDeniedException ends the `Flux<>`.

The `@EnforceTillDenied` annotation cannot be combined with any other
enforcement annotation.

# `@EnforceDropWhileDenied`

The `@EnforceDropWhileDenied` annotation wraps the `Flux<>` in a PEP.

The access control only starts when a subscriber subscribes to the wrapped
`Flux<>`, not at construction time of the `Flux<>`.

The basic concept of the `@EnforceDropWhileDenied` PEP is to grant access to
the `FLux<>` upon an initial `PERMIT` decision and to grant access until the
client cancels the subscription, or the original `Flux<>` completes. However,
whenever a non-`PERMIT` decision is received, all messages are dropped from the
`Flux<>` until a new `PERMIT` decision is received.

The subscriber will not be made aware of the fact that events are dropped
from the stream.

Upon the initial `PERMIT`, the PEP subscribes to the original `Flux<>`. During
access to the `Flux<>`, all constraints are enforced.

Upon receiving a new `PERMIT` decision with different constraints, the
constraint handling is updated accordingly.

Upon receiving a non-`PERMIT` decision, the constraints are enforced, and
messages are dropped without sending a `AccessDeniedException` downstream. The
date resumes on receiving a new `PERMIT` decision.

The `@EnforceDropWhileDenied` annotation cannot be combined with any other
enforcement annotation.

# `EnforceRecoverableIfDenied` 

The `@EnforceRecoverableIfDenied` annotation wraps the `Flux<>` in a PEP.

The access control only starts when a subscriber subscribes to the wrapped
`Flux<>`, not at construction time of the `Flux<>`.

The basic concept of the @EnforceRecoverableIfDenied PEP is to grant access
to the `FLux<>` upon an initial `PERMIT` decision and to grant access until the
client cancels the subscription, or the original `Flux<>` completes. However,
whenever a non-`PERMIT` decision is received, all messages are dropped from the
`Flux<>` until a new `PERMIT` decision is received.

The subscriber will be made not be made aware of the fact that events are
dropped from the stream by sending `AccessDeniedExceptions` on a non-`PERMIT`
decision.

The subscriber can then decide to stay subscribed via `.onErrorContinue()`.
Without .onErrorContinue this behaves similar to to `@EnforceTillDenied`. With
`.onErrorContinue()` this behaves similar to `@EnforceDropWhileDenied`, however
the subscriber can explicitly handle the event that access is denied and
choose to stay subscribed or not.

The `@EnforceRecoverableIfDenied` annotation cannot be combined with any other
enforcement annotation.
