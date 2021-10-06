# Demo - Writing Custom extensions for a PDP

This demo shows how to write custom Policy Information Points (PIPs) and function libraries.

Once you build this project, there will be a `sapl-demo-extension-0.0.1-SNAPSHOT-jar-with-dependencies.jar` available in the target folder.

This JAR can be deployed with a sapl-server-lt or sapl-server-ce. 

If you are running the servers in a docker/kubernetes environment, please refer to the Readme files of the respective servers:

* [Sapl Server LT](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-server-lt)
* [Sapl Server CE](https://github.com/heutelbeck/sapl-server/tree/main/sapl-server-ce)

If you want to test this with a locally running server, the process differs a little bit. Locally you cannot start the server using `mvn spring-boot:run` or the equivalent tools of your IDE. This is due to how Spring sets up class loading in this case. You must invoke the server by running the JAR directly and by providing the path where the `sapl-demo-extension-0.0.1-SNAPSHOT-jar-with-dependencies.jar` is located.

The way you have to enter the command to run the server depends on both the OS and the shell you are using. The basic command is the same everywhere, however the way you have to escape certain strings in the command differs from shell to shell.

For example for running the servers (here we use the LT server, the command for CE is analogous) on Windows using the traditional Windows command prompt is:

```
   java  -Dloader.path="c:\PATH TO JAR WITH DEPENDECIES" -jar .\sapl-server-lt-2.0.0-SNAPSHOT.jar
```

Under PowerShell the strings are escaped differently:

```
   java  -D'loader.path'='c:\PATH TO JAR WITH DEPENDECIES' -jar .\sapl-server-lt-2.0.0-SNAPSHOT.jar
```

Please take a look at the POM file of this project. There all dependencies and build steps are explained in detail. 

For quick testing of the new extensions in the PDP server you can use the `sapl-demo-remote` project. This is pre-configured with the credentials for a Server LT ran the way explained above.

There you find the following lines of code: 

```java
		var authzSubscription = AuthorizationSubscription.of("Willi", "eat", "icecream");
		LOG.info("Subscription: {}", authzSubscription);
		/*
		 * This just consumes the first decision in a blocking fashion to quickly
		 * terminate the demo application. If not using blockFirst() or take(1), the
		 * Flux will continue to listen to the PDP server and receive updated
		 * authorization decisions when applicable. For alternative patterns of
		 * invocation, consult the sapl-demo-pdp-embedded
		 */
		pdp.decide(authzSubscription).doOnNext(decision -> LOG.info("Decision: {}", decision)).subscribe();
		Thread.sleep(60*1000);
		return 0;
```

Here you can customize the authorization subscription. E.g., replace the resource value `"icecream"` with the IP address of your mobile phone in your local network. 
Then deploy the following policy in your PDP servers policy directory (default: `%HOME%\sapl\policies´):

```
policy "Permit if device reachable"
permit
where 
  resource.<demo.reachable(500,300)>;
```

This policy will ping the host every 500ms and return false if it is not reachable within 300ms. The decision will only change if the reachability status changes. If you now run the sapl-demo-remote code with you phones IP address in the subscriptions resource field, you can see the decision changing when you turn on and off the WiFi connection of your phone. Of course this will only work, if your test machine and your phone are in the same local network.

In case you do not want to deploy the extensions with a Server, but with an embedded PDP, you have to declare an dependency in your projects POM to include the module containing your PIP classes. You could alternatively just put the source of the extensions directly in your applications module.
To instantiate the extensions there are two possibilities. 

a) Spring application: Make sure to have the PIPs and function library classes as Beans in your application context. The PDP will pick them up automatically. Of course the classes/configuration have to be in packages which are scanned by spring. This can for example be done by having them in a package below your applications main class, or by explicitly adding the respective packages to the component scan (`@ComponentScan` annotation).

b) Plain Java application: You have to instantiate the extension classes and hand them over to the `PolicyDecisionPointFactory`:

```java
	EmbeddedPolicyDecisionPoint pdp;
		if (path != null) {
			pdp = PolicyDecisionPointFactory.filesystemPolicyDecisionPoint(path, List.of(new EchoPIP()),
					List.of(new SimpleFunctionLibrary()));
		}
```
