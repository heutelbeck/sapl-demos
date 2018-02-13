# Submodule sapl-demo-obligation-advice

This submodule explains how to use Obligation and Advice Handlers, which take care of the obligations and advice encountered while evaluating a SAPL policy. First there will be a tutorial on how to easily use obligation and advice handlers in your own application. More technical details about how to customize the way your Obligation and Advice Handler are called by the `ObligationHandlerService` or the `AdviceHandlerService` will be given afterwards.

## Tutorial for using Obligation and Advice Handlers

First of all you need to include the `sapl-spring-boot-starter` in your maven project. To do so, add the following dependency:

```java
<dependency>
        <groupId>io.sapl</groupId>
        <artifactId>sapl-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
</dependency>
```

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
