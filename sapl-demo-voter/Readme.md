# Using SAPL policies in Spring Security with an AccessDecisionVoter approach

## Introduction

In Spring applications access control can be established in a number of different ways. In this tutorial, we demonstrate how to hook a SAPL-based 
policy decision point (PEP) into the access voting mechanism of Spring Security and to have SAPL cast its vote alongside with other existing 
`AccessDecisionVoter` implementations.
  

## Dependencies

For this tutorial we are building a Spring Boot application. We have to add a dependency on the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter):

```xml
<dependency>
   <groupId>io.sapl</groupId>
   <artifactId>sapl-spring-boot-starter</artifactId>
   <version>2.0.0-SNAPSHOT</version>
</dependency>
```

As the module is currently not hosted on the central Maven repositories add the matching repository to you POM as well:

```xml
<repositories>
   <repository>
      <id>sapl</id>
      <name>SAPL Release Repository</name>
      <url>http://repo.sapl.io/releases</url>
   </repository>
   <repository>
      <id>sapl-snapshots</id>
      <name>SAPL Snapshot Repository</name>
      <url>http://repo.sapl.io/snapshots</url>
      <snapshots>
         <enabled>true</enabled>
         <updatePolicy>always</updatePolicy>
      </snapshots>
   </repository>
</repositories>
```

## Configuring the application

Using the starter provides a `PolicyDecisionPoint (PDP)`. By default, policies will be loaded from the `/policies` folder in the application resources on startup.
When using the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) , a bean of type `SaplBasedVoter` will 
be automatically provided.  

An application can now customize its voters to integrate `SaplAccessDecisionVoter` by setting up its own `AccessDecisionManager` in its security configuration. 
The `AccessDecisionManager` will be asked by the Spring runtime to authorize   

```java
	@Bean
	public AccessDecisionManager getAccessDecisionManager(SaplAccessDecisionVoter saplAccessDecisionVoter) {

		List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
				// The WebExpressionVoter enables us to use SpEL (Spring Expression Language) to
				// authorize the requests using the @PreAuthorize annotation.
				new WebExpressionVoter(),
				// The RoleVoter votes if any of the configuration attributes starts with the
				// String “ROLE_”.
				new RoleVoter(),
				// The AuthenticatedVoter will cast a vote based on the Authentication object’s
				// level of authentication – specifically looking for either a fully
				// authenticated principal
				new AuthenticatedVoter(),
				// Finally add the autoconfigured SaplBasedVoter

				saplAccessDecisionVoter);

		// Now select a AccessDecisionManager implementation based on the individual votes of 
		// all voters are supposed to be combined into a final decision.
		//
		// There are three different choices:
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
You need to utilize this AccessDecisionManager then for your authentication (and authorization):

```java
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.trace("start configuring...");
		http
				.httpBasic().and()
				.authorizeRequests()
					.anyRequest().fullyAuthenticated()
					//this is the access decision manager 
					//that we created including our voter
					.accessDecisionManager(adm)
				.and()
				.formLogin()
					.loginPage("/login").permitAll()
				.and()
					.logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
				.and()
					.httpBasic()
				.and()
					.csrf().disable()
				;
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
Tip: If you add the line `logging.level.io.sapl=DEBUG` to your `application.properties` you will get useful messages that shows you the parameters that are actually provided to the `SAPLAuthorizer`, which can help you write your policies.
