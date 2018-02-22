# Submodule  sapl-demo-voter

This demo shows how to vote for or against incoming Requests with a SAPL based voter that uses Sapl Policies. 

## Tutorial for using the Voter

Obtaining a decision from SAPL Policies we need a `PolicyDecisionPoint`(`PDP`). A `PDP` as a `bean`  is  available as dependency for
a Spring Boot Starter Project, configured in the submodule [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter)
from project <https://github.com/heutelbeck/sapl-policy-engine> .
Remote or embedded `PDP` can be integrated into a Spring Boot Project with:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```


When using the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) , a bean of type `SaplBasedVoter` will be automatically provided. To use it, you only have to add it to your `SecurityConfig` by adding a `AccessDecisionManager` which uses it. Here you can see an example of a complete configuration:

```java
	@Bean
	public AccessDecisionManager getAccessDecisionManager(SaplBasedVoter saplBasedVoter) {

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
				// The WebExpressionVoter enables us to use SpEL (Spring Expression Language) to
				// authorize the requests using the @PreAuthorize annotation.
				new WebExpressionVoter(),
				// The RoleVoter votes if any of the configuration attributes starts with the
				// String “ROLE_”.
				new RoleVoter(),
				// The AuthenticatedVoter will cast a vote based on the Authentication object’s
				// level of authentication – specifically looking for either a fully
				// authenticated pricipal
				new AuthenticatedVoter(),
				// this one is our own, we use SAPL and PDP as base here

				saplBasedVoter);

		// there are basically three different choices:
		// - AffirmativeBased – grants access if any of the AccessDecisionVoters return
		// an affirmative vote
		// - ConsensusBased – grants access if there are more affirmative votes than
		// negative (ignoring users who abstain)
		// - UnanimousBased – grants access if every voter either abstains or returns an
		// affirmative vote

		return new UnanimousBased(decisionVoters);
	}
```

Furthermore, you need to provide the SaplBasedVoter to the application:

```java	
	@Bean
	public SaplBasedVoter saplBasedVoter() {
		return new SaplBasedVoter(saplAuthorizer);
	}
```

Now you can filter incoming Requests using Sapl Policies. In the SaplBasedVoter the following pattern is used:

```java
Response decision = pep.getResponse(new AuthenticationSubject(authentication), new HttpAction(request),
				new HttpResource(request));
```

This needs to be mapped to the Spring based Voter Decisions which is done via:
```java
private int mapDecisionToVoteResponse(Response response) {
		int returnValue;
		switch (response.getDecision()) {
		case PERMIT:
			returnValue = ACCESS_GRANTED;
			break;
		case DENY:
			returnValue = ACCESS_DENIED;
			break;
		case INDETERMINATE:
		case NOT_APPLICABLE:
			returnValue = ACCESS_ABSTAIN;
			break;
		default:
			returnValue = ACCESS_GRANTED;
			break;
		}
		return returnValue;
	}
```
Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizator`, which can help you write your policies.
