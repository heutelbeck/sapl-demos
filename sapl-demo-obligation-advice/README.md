# Tutorial sapl-demo-obligation-advice

This submodule explains how to use Obligation and Advice Handlers, which take care of the obligations and advice encountered while evaluating a SAPL policy. First there will be a tutorial on how to easily use obligation and advice handlers in your own application. More technical details about how to customize the way your Obligation and Advice Handler are called by the [ObligationHandlerService](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/marshall/obligation/ObligationHandlerService.java) or the [AdviceHandlerService](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/marshall/advice/AdviceHandlerService.java) and about what you have to know when implementing your own [SAPLAuthorizator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLAuthorizator.java) will be given afterwards.
Please note that we are using Lombok logging for all Demo Projects.

## Tutorial for using Obligation and Advice Handlers

First of all you need to include the [sapl-spring-boot-starter](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-spring-boot-starter) in your maven project. To do so, add the following dependency:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Please note that if you are using your own [SAPLAuthorizator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLAuthorizator.java)  and not the one provided by the `sapl-spring-boot-starter` you have to manually include the advice and obligation handling.


### Obligation Handlers


Next you need to write your own Obligation Handler. In this tutorial we want to write an Obligation Handler for this policy:

```java
policy "permit_attending_nurse_see_diagnosis"
permit
   action.method == "readDiagnosis"
where
   subject.name == resource.attendingNurse;
obligation
   { "type" : "sendEmail",
     "recipient" : "supervisor@example.org",
     "subject" : "Diagnosis access by nurse",
     "message" : subject.name + " has looked up the diagnosis of " + resource.name
   }
```

Please note that an obligation is simply the written form of a Json Object. To handle this obligation we can now use the following obligation handler:

```java
@Slf4j
public class EmailObligationHandler implements ObligationHandler {


	@Override
	public void handleObligation(Obligation obligation) throws ObligationFailed {
		JsonNode obNode = obligation.getJsonObligation();
		if (obNode.has("recipient") && obNode.has("subject") && obNode.has("message")) {
			sendEmail(obNode.findValue("recipient").asText(), obNode.findValue("subject").asText(),
					obNode.findValue("message").asText());
		} else {
			throw new ObligationFailed();
		}

	}

	@Override
	public boolean canHandle(Obligation obligation) {
		JsonNode obNode = obligation.getJsonObligation();
		if (obNode.has("type")) {
			String type = obNode.findValue("type").asText();
			if ("sendEmail".equals(type)) {
				return true;
			}
		}
		return false;
	}

	private static void sendEmail(String recipient, String subject, String message) {
		LOGGER.info("An E-Mail has been sent to {} with the subject '{}' and the message '{}'.", recipient, subject,
				message);
	}

}
```
Note that the `EmailObligationHandler` has to override two methods. First we override `canHandle`. This method returns `true`, if the said Obligation Handler can handle an obligation. In this example we check that the obligation has the right `type`, but of course you can put here anything that fits with your style of writing obligations.
Second we override `handleObligation`. In this method the actual obligation handling is performed. Please note that in the case an obligation could not be handled correctly an `ObligationFailed` is thrown. This is important to ensure that in this case the Response will be changed to `DENY`. The same thing will happen if no Obligation Handler is found that can handle a certain obligation.

Last but not least you have to register your Obligation Handler to an `ObligationHandlerService` and provide this `ObligationHandlerService` to your Spring Application. The following Bean demonstrates how to do this:

```java
@Bean
	public SimpleObligationHandlerService getObligationHandlers() {
		SimpleObligationHandlerService sohs = new SimpleObligationHandlerService();
		sohs.register(new EmailObligationHandler());
		sohs.register(new CoffeeObligationHandler());
		sohs.register(new SimpleLoggingObligationHandler());
		return sohs;
	}
```
Here various Obligation Handlers are registered to a `SimpleObligationHandlerService`, which can be imported from the `sapl-spring` module. If you want to write your own ObligationHandlerService be sure that it implements the Interface `ObligationHandlerService`.

### Advice Handlers

The use of Advice Handlers is similar to that of the Obligation Handlers. The only difference is, that we don't throw an Exception if the advice couldn't be handled, because the handling of an advice isn't mandatory. Here you can see the code for an Advice Handler:

Policy:

```java
policy "permit_attending_doctor_see_diagnosis"
permit
   action.method == "readDiagnosis"
where
   subject.name == resource.attendingDoctor;
advice
   { "type" : "simpleLogging",
     "message" : subject.name + " has looked up the diagnosis of " + resource.name
   }
```

Advice Handler:

```java
@Slf4j
public class SimpleLoggingAdviceHandler implements AdviceHandler {
	
	@Override
	public void handleAdvice(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("message")) {
			LOGGER.info(advice.getJsonAdvice().findValue("message").asText());
		}

	}

	@Override
	public boolean canHandle(Advice advice) {
		JsonNode adNode = advice.getJsonAdvice();
		if (adNode.has("type")) {
			String type = adNode.findValue("type").asText();
			if ("simpleLogging".equals(type)) {
				return true;
			}
		}
		return false;
	}

}
```

Registration:

```java
@Bean
public SimpleAdviceHandlerService setAdviceHandlers() {
	SimpleAdviceHandlerService sahs = new SimpleAdviceHandlerService();
	sahs.register(new EmailAdviceHandler());
	sahs.register(new SimpleLoggingAdviceHandler());
	return sahs;
	
```

If you want to write your own AdviceHandlerService, implement the interface `AdviceHandlerService`.



## Advanced Customization: ObligationHandlerService, AdviceHandlerService and SAPLAuthorizator

### A closer look on ObligationHandlerService and AdviceHandlerService

If you want to change the way, your Obligation Handlers are called, you should implement the interface [ObligationHandlerService](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/marshall/obligation/ObligationHandlerService.java):

```java
public interface ObligationHandlerService {

	/**
	 * register a new obligationHandler
	 * 
	 * @param obligationHandler
	 *            - the obligation to register
	 */
	void register(ObligationHandler obligationHandler);

	/**
	 * unregister an ObligationHandler
	 * 
	 * @param obligationHandler
	 *            - the obligation to register
	 */
	void unregister(ObligationHandler obligationHandler);

	/**
	 * unregister all ObligationHandlers
	 */
	void unregisterAll();

	/**
	 * 
	 * @return List of all registered handlers
	 */
	List<ObligationHandler> registeredHandlers();

	/**
	 * How to handle the case, where no suitable handler for an obligation is
	 * available
	 */
	default void onNoHandlerAvailable() throws ObligationFailed {
		throw new ObligationFailed("no suitable handler registered in service");
	}

	/**
	 * implements strategy to choose the handler from the registered. <br/>
	 * Should especially cover cases, where more than one handler is suitable for an
	 * obligation
	 * 
	 * @param obligation
	 *            - the obligation
	 * @return Optional of the handler to use for the obligation. Empty, if none
	 *         found
	 */
	default Optional<ObligationHandler> chooseHandler(Obligation obligation) {
		Optional<ObligationHandler> returnHandler = registeredHandlers().stream()
				.filter(handler -> handler.canHandle(obligation)).findAny();
		return returnHandler;
	}

	/**
	 * Handle an Obligations
	 * 
	 * @param obligation
	 *            - the obligation to handle
	 * @throws ObligationFailed
	 *             - maybe thrown by the used {@link ObligationHandler}
	 */
	default void handle(Obligation obligation) throws ObligationFailed {
		Optional<ObligationHandler> handler = chooseHandler(obligation);
		if (handler.isPresent()) {
			handler.get().handleObligation(obligation);
		} else {
			onNoHandlerAvailable();
		}
	}

}
```

An easy example how to do it can be found in the `SimpleObligationHandlerService`:

```java
public class SimpleObligationHandlerService implements ObligationHandlerService {

	private final List<ObligationHandler> handlers = new LinkedList<>();

	@Override
	public void register(ObligationHandler obligationHandler) {
		handlers.add(obligationHandler);
	}

	@Override
	public void unregister(ObligationHandler obligationHandler) {
		handlers.remove(obligationHandler);
	}

	@Override
	public List<ObligationHandler> registeredHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	@Override
	public void unregisterAll() {
		handlers.clear();
	}

}
```

The interface [AdviceHandlerService](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/marshall/advice/AdviceHandlerService.java) is quite similar to the above:

```java
public interface AdviceHandlerService {

	/**
	 * register a new AdviceHandler
	 * 
	 * @param AdviceHandler
	 *            - the AdviceHandler to register
	 */
	void register(AdviceHandler adviceHandler);

	/**
	 * unregister an AdviceHandler
	 * 
	 * @param AdviceHandler
	 *            - the AdviceHandler to register
	 */
	void unregister(AdviceHandler adviceHandler);

	/**
	 * unregister all AdviceHandlers
	 */
	void unregisterAll();

	/**
	 * 
	 * @return List of all registered handlers
	 */
	List<AdviceHandler> registeredHandlers();

	/**
	 * implements strategy to choose the handler from the registered. <br/>
	 * Should especially cover cases, where more than one handler is suitable for an
	 * advice
	 * 
	 * @param advice
	 *            - the advice
	 * @return Optional of the handler to use for the advice. Empty, if none found
	 */
	default Optional<AdviceHandler> chooseHandler(Advice advice) {
		Optional<AdviceHandler> returnHandler = registeredHandlers().stream()
				.filter(handler -> handler.canHandle(advice)).findAny();
		return returnHandler;
	}

	/**
	 * Handle an Advice
	 * 
	 * @param advice
	 *            - the advice to handle
	 */
	default void handle(Advice advice) {
		Optional<AdviceHandler> handler = chooseHandler(advice);
		if (handler.isPresent()) {
			handler.get().handleAdvice(advice);
		}
	}

}
```

The `SimpleAdviceHandlerService`, which is used by default:

```java
public class SimpleAdviceHandlerService implements AdviceHandlerService {

	private final List<AdviceHandler> handlers = new LinkedList<>();

	@Override
	public void register(AdviceHandler adviceHandler) {
		handlers.add(adviceHandler);
	}

	@Override
	public void unregister(AdviceHandler adviceHandler) {
		handlers.remove(adviceHandler);
	}

	@Override
	public List<AdviceHandler> registeredHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	@Override
	public void unregisterAll() {
		handlers.clear();
	}

}
```


### Obligation and Advice in the SAPLAuthorizator

Obligations and advice are handled through the [SAPLAuthorizator](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-spring/src/main/java/io/sapl/spring/SAPLAuthorizator.java) . Therefore if you don't use this `SAPLAuthorizator` in your Application, you have to find another way to handle them. Here you can have a look at how this works inside the `SAPLAuthorizator`:

```java

@Slf4j
public class SAPLAuthorizator {

	protected final PolicyDecisionPoint pdp;

	protected final ObligationHandlerService obs;
	
	protected final AdviceHandlerService ahs;

	@Autowired
	public SAPLAuthorizator(PolicyDecisionPoint pdp, ObligationHandlerService obs, AdviceHandlerService ahs) {
		this.pdp = pdp;
		this.obs = obs;
		this.ahs = ahs;
	}

	public boolean authorize(Subject subject, Action action, Resource resource) {
		LOGGER.trace("Entering hasPermission(Subject subject, Action action, Resource resource)...");
		Response response = runPolicyCheck(subject.getAsJson(), action.getAsJson(), resource.getAsJson());
		LOGGER.debug("Response decision ist: {}", response.getDecision());
		return response.getDecision() == Decision.PERMIT;
	}

	public Response getResponse(Subject subject, Action action, Resource resource) {
		LOGGER.trace("Entering getResponse...");
		Response response = runPolicyCheck(subject.getAsJson(), action.getAsJson(), resource.getAsJson());
		return response;
	}

	protected Response runPolicyCheck(Object subject, Object action, Object resource) {
		LOGGER.trace("Entering runPolicyCheck...");
		LOGGER.debug("These are the parameters: \n  subject:{} \n  action:{} \n  resource:{}", subject, action,
				resource);

		Response response = pdp.decide(subject, action, resource);

		LOGGER.debug("Here comes the response: {}", response);
		if (response.getObligation().orElse(null) != null) {

			List<Obligation> obligationsList = Obligation.fromJson(response.getObligation().get());
			LOGGER.debug("Start handling obligations {}", obligationsList);
			try {
				for (Obligation o : obligationsList) {
					LOGGER.debug("Handling now {}", o);
					obs.handle(o);
				}
			} catch (ObligationFailed e) {
				response = new Response(Decision.DENY, null, null, null);
			}
		}
		
		if (response.getAdvice().orElse(null) != null) {
			
			List<Advice> adviceList = Advice.fromJson(response.getAdvice().get());
			
			LOGGER.debug("Start handling advices {}", adviceList);

			for (Advice a : adviceList) {
				LOGGER.debug("Handling now {}", a);
				ahs.handle(a);
			}

		}

		return response;
	}

}
```


