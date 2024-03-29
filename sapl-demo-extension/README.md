# Demo - Writing Custom extensions for a PDP

This demo shows how to write custom Policy Information Points (PIPs) and function libraries.

Once you build this project, there will be a `sapl-demo-extension-2.1.0-SNAPSHOT-jar-with-dependencies.jar` available in the target folder.

This JAR can be deployed with a sapl-server-lt or sapl-server-ce. 

If you are running the servers in a docker/kubernetes environment, please refer to the Readme files of the respective servers:

* [Sapl Server LT](https://github.com/heutelbeck/sapl-policy-engine/tree/master/sapl-server-lt)
* [Sapl Server CE](https://github.com/heutelbeck/sapl-server/tree/main/sapl-server-ce)

If you want to test this with a locally running server, the process differs a little. Locally you cannot start the server using `mvn spring-boot:run` or the equivalent tools of your IDE. This is due to how Spring sets up class loading in this case. You must invoke the server by running the JAR directly and providing  the path where the `sapl-demo-extension-2.1.0-SNAPSHOT-jar-with-dependencies.jar` is located.

The way you have to enter the command to run the server depends on both the OS and the shell you are using. The basic command is the same everywhere. However, the way you have to escape certain strings in the command differs from shell to shell.

For example, for running the servers (here we use the LT server, the command for CE is analogous) on Windows using the traditional Windows command prompt is:

```
   java  -Dloader.path="c:\PATH TO JAR WITH DEPENDECIES FOLDER" -jar .\sapl-server-lt-2.1.0-SNAPSHOT.jar
```

Under PowerShell the strings are escaped differently:

```
   java  -D'loader.path'='c:\PATH TO JAR WITH DEPENDECIES FOLDER' -jar .\sapl-server-lt-2.1.0-SNAPSHOT.jar
```

Under Linux and Bash the command can be:

```
   java  -Dloader.path=file:/PATH_TO_JAR_WITH_DEPENDECIES_FOLDER -jar .\sapl-server-lt-2.1.0-SNAPSHOT.jar
```

Please take a look at the POM file of this project. There all dependencies and build steps are explained in detail. 

For quick testing of the new extensions in the PDP server, you can use the `sapl-demo-remote` project. This is pre-configured with the credentials for a Server LT run the way explained above.

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

This policy will ping the host every 500ms and return false if it is not reachable within 300ms. The decision will only change if the reachability status changes. If you now run the sapl-demo-remote code with your phone's IP address in the subscription's resource field, you can see the decision changing when you turn on and off your phone's Wi-Fi connection. Of course, this will only work if your test machine and your phone are in the same local network. There may also be some differences with phone settings and local routing that may prevent this from working. However, this is only a teaching example and should get across how the custom PIP and policies may interact.

If you do not want to deploy the extensions with a Server, but with an embedded PDP, you must declare a dependency in your project's POM to include the module containing your PIP classes. You could alternatively just put the source of the extensions directly in your application's module.
There are two ways to instantiate the extensions:

a) Spring application: Make sure to have the PIPs and function library classes as Beans in your application context. The PDP will pick them up automatically. Of course, Spring hast to scan and detect the classes/configuration. 
To ensure that the scanning happens, put the Beans in a package below your application's main class, or add the respective packages explicitly to the component scan (`@ComponentScan` annotation).

b) Plain Java application: You have to instantiate the extension classes and hand them over to the `PolicyDecisionPointFactory`:

```java
	EmbeddedPolicyDecisionPoint pdp;
		if (path != null) {
			pdp = PolicyDecisionPointFactory.filesystemPolicyDecisionPoint(path, List.of(new EchoPIP()),
					List.of(new SimpleFunctionLibrary()));
		}
```

Finally, this extension also supplies a simple function library and exposes the functions `simple.length` and `simple.append`. The function `length` returns the length of a string or array, while `append` concatenates an arbitrary number of strings and numbers into a single string.


*Note*: Developers must add  `-parameters` parameter to the compilation to ensure that the automatically generated documentation does contain the names of the parameters used in the methods.

