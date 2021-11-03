# Demo of using a remote PDP

## Overview

The core of a system using Attribute Stream Based Access Control (ASBAC) is the so-called Policy Decision Point (PDP).

In this demo, we will use a client to connect to a dedicated PDP server.

## Running the Demo

For running the demo, a local install of JDK 11 or newer and Maven are required.

Also, a PDP server has to be running. Please refer to the [SAPL Server LT](https://github.com/heutelbeck/sapl-policy-engine/blob/master/sapl-server-lt/README.md) for an easy way to run a demo server.

First, build this demo by changing into the `sapl-demo-remote` folder and executing the command:

```
mvn install
```

After the build completes, the `target` folder contains the executable JAR.
Change into this folder and execute the following command to run the demo:

```
java -jar sapl-demo-remote-2.0.0-SNAPSHOT-jar-with-dependencies.jar
```

By default, the demo attempts so connect to `127.0.0.1:8443` and uses the default client 
credentials configured in the SAPL Server LT for demo purposes.

The demo accepts the following command line parameters:

```
  -h, -host=<host>       Hostname of the policy decision point including prefix
                           and port. E.g. 'https://example.org:8443'.
  -k, -key=<clientKey>   Client key for the demo application, to be obtained
                           from the PDP administrator.
  -s, -secret=<clientSecret>
                         Client secret for the demo application, to be obtained
                           from the PDP administrator.
```

The demo will send the following authorization subscription to the server:

```json
{
	"subject": "Willi",
	"action": "eat",
	"resource": "icecream"
}
```

The demo will now output some messages on the console showing the incoming decisions or log the errors to connect to the server.

The demo will run until the PDP decides there will be no further decisions or until the user manually stops the process (e.g., `CTRL-C`).

If the demo fails to connect to the server:

```
[main] WARN org.demo.RemotePDPDemo - INSECURE SSL SETTINGS! This demo uses an insecure SslContext for testing purposes only. It will accept all certificates. This is only for testing local servers with self-signed certificates easily. NERVER USE SUCH A CONFIURATION IN PRODUCTION!
[main] INFO org.demo.RemotePDPDemo - Subscription: AuthorizationSubscription(subject="Willi", action="eat", resource="icecream", environment=null)
[reactor-http-nio-2] ERROR io.sapl.pdp.remote.RemotePolicyDecisionPoint - Error : Connection refused: no further information: localhost/127.0.0.1:8443; nested exception is io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: localhost/127.0.0.1:8443
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: AuthorizationDecision(decision=INDETERMINATE, resource=Optional.empty, obligations=Optional.empty, advice=Optional.empty)
[reactor-http-nio-3] ERROR io.sapl.pdp.remote.RemotePolicyDecisionPoint - Error : Connection refused: no further information: localhost/127.0.0.1:8443; nested exception is io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: localhost/127.0.0.1:8443
[reactor-http-nio-4] ERROR io.sapl.pdp.remote.RemotePolicyDecisionPoint - Error : Connection refused: no further information: localhost/127.0.0.1:8443; nested exception is io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: localhost/127.0.0.1:8443
...
```

If the server is available and has matching policies (e.g., a time-based policy):

```
[main] WARN org.demo.RemotePDPDemo - INSECURE SSL SETTINGS! This demo uses an insecure SslContext for testing purposes only. It will accept all certificates. This is only for testing local servers with self-signed certificates easily. NERVER USE SUCH A CONFIURATION IN PRODUCTION!
[main] INFO org.demo.RemotePDPDemo - Subscription: AuthorizationSubscription(subject="Willi", action="eat", resource="icecream", environment=null)
[reactor-http-nio-2] ERROR io.sapl.pdp.remote.RemotePolicyDecisionPoint - Error : Connection refused: no further information: localhost/127.0.0.1:8443; nested exception is io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: localhost/127.0.0.1:8443
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: AuthorizationDecision(decision=PERMIT, resource=Optional.empty, obligations=Optional.empty, advice=Optional.empty)
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: AuthorizationDecision(decision=DENY, resource=Optional.empty, obligations=Optional.empty, advice=Optional.empty)
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: AuthorizationDecision(decision=PERMIT, resource=Optional.empty, obligations=Optional.empty, advice=Optional.empty)
[reactor-http-nio-2] INFO org.demo.RemotePDPDemo - Decision: AuthorizationDecision(decision=DENY, resource=Optional.empty, obligations=Optional.empty, advice=Optional.empty)
...
```
