# Tutorial sapl-demo-reactive

This sub-project demonstrates the different possibilities to use the reactive API of the
[SAPL Policy Engine](https://github.com/heutelbeck/sapl-policy-engine).
A Policy Decision Point (PDP) can be integrated in two ways:
1) either directly embedded in the application (embedded PDP),
2) or running on a separate server (remote PDP).

### Using the embedded PDP
This type of integration is the simpler one, but is also somewhat less flexible than integration via a remote
PDP server.

The files with the policies can be stored either in the classpath of the application or in an arbitrary directory
in the file system. If nothing in this regard is configured in the file `src/main/resources/application.properties`,
the policies are retrieved from the `/policies` directory in the classpath. This is where this demo-application looks
up the policies too. If the directory is to be changed, this can be done in the file `application.properties` as follows:
```properties
io.sapl.type=resources
io.sapl.resources.policies-path=/path/to/other/directory
```
However, if the policy files are located in a directory outside the classpath, the following configuration must be used:
```properties
io.sapl.type=filesystem
io.sapl.filesystem.policies-path=/absolute/path/to/policy/directory
```
If no directory path is configured in the second variant, the policies will be searched under `~/policies`
(Windows: `${user.home}/policies`).

To start the demo application directly from within the IDE, just start the Spring-Boot application
`org.demo.DemoApplication`, open your browser and navigate to the URL `http://localhost:8080/demo`.

### Using the remote PDP
The demo application can also use a Policy Decision Point running on a separate server. A corresponding server must be 
started. For the purposes of this demo application, the PDP server from the project
[sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine) can be used. After having downloaded the project,
just start the Spring-Boot application `io.sapl.pdp.server.PDPServerApplication` in the sub-project `sapl-pdp-server`. The
PDP will look for the policy files in the directory `~/sapl/policies`. Before the server is started, the policy files under
`src/main/resources/remote-policies` in this demo application project must be copied to `~/sapl/policies`. These policies 
distinguish in one point of those found under `src/main/resources/policies`: they cannot use the Policy Information Point
`org.demo.pip.PatientPIP` directly (as it is part of this demo project and therefore not known to the policy engine). Instead
the policies have to call the `PatientPIP` via the `HTTPPolicyInformationPoint`, which is provided by the SAPL policy engine.
Therefore a PIP server must be runnig, providing the functionality of the `PatientPIP` via corresponding REST endpoints. Such
a server is available in the sub-project `sapl-demo-pip-server` of this demo project. It can be started via the Spring-Boot
application `org.demo.PipServerApplication`. Just make sure that it is running on a port other than the ones used by the demo 
application and the PDP server. The configured port 8081 is just fine. If you modify it, you also have to adjust the URLs in
the remote policy files.

Now that both the PDP server from the project [sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine)
and the PIP server from the demo sub-project `sapl-demo-pip-server` have been started, the demo application can be started next.
But for it to use the remote PDP, it must be configured in `application.properties` as follows:
```properties
io.sapl.type=remote
io.sapl.remote.host=localhost
io.sapl.remote.port=8443
io.sapl.remote.key=YJidgyT2mfdkbmL
io.sapl.remote.secret=Fa4zvYQdiwHZVXh
```
The PDP server of the [sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine) project uses basic authentication 
to authenticate clients. The configuration values `io.sapl.remote.key` and `io.sapl.remote.secret` must match the corresponding 
values from the configuration of the PDP server (`http.basic.auth.client-key` and `http.basic.auth.client-secret`).

### The demo application
The demo application shows how a [Vaadin](https://vaadin.com/framework) web application can be integrated with
[Spring Security](https://spring.io/projects/spring-security) and the [SAPL Policy Engine](https://github.com/heutelbeck/sapl-policy-engine).
It presents the use of the reactive Policy Decision Point and the reactive Policy Enforcement Point, as well as the use of reactive
multi-requests.

After the user has logged in with the password `password`, four buttons are presented on the initial page of the application. The first
two buttons `Show Patient List (blocking, single requests)` and `Show Patient List (blocking, multi-requests)` lead to the fictitious
healthcare scenario, which is also the basis of the demo application in spring-mvc-app. It demonstrates the blocking use of the PDP.
Although the Policy Decision Points of the SAPL policy engine are implemented reactively, applications can still use them in a blocking
way. This applies to both simple requests and multi-requests. The variant accessed via the second button uses multi-requests wherever
possible. The corresponding performance improvement compared to the single requests is clearly noticeable when using the remote PDP.

The buttons labeled `Show Reactive View (single requests)` and `Show Reactive View (multi-request)` show a page that connects data 
streams (pulse rate and blood pressure data) to the authorization decision streams of the PDP (non-blocking). This makes changes
in the authorization decisions immediately visible without the application having to poll. Simple time-dependent policies are used
to demonstrate this. Doctors and nurses get to see pulse rate data when the number of seconds of the current time is greater than 4.
Blood pressure data is always displayed when the number of seconds of the current time is less than 31 or greater than 35. Visitors
only see pulse rate data, and also less frequently than doctors and nurses.

If the policy files are not stored in the classpath, but in any directory of the filesystem (`io.sapl.type=filesystem`, see above),
it is possible in all four variants to modify the policies while the application is running. The changes have an immediate effect
on the reactive variants and on the blocking variants after a page refresh. Restarting the demo application or the PDP server is 
no longer necessary in either case.
