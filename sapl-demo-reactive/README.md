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
io.sapl.pdp-type=embedded
io.sapl.prp-type=resources
io.sapl.resources.policies-path=/path/to/other/directory
```
However, if the policy files are located in a directory outside the classpath, the following configuration must be used:
```properties
io.sapl.prp-type=filesystem
io.sapl.filesystem.policies-path=/absolute/path/to/policy/directory
```
If no directory path is configured in the second variant, the policies will be searched under `~/policies`
(Windows: `${user.home}/policies`).

The same mechanism and defaults hold for the configuration file `pdp.json`, where the policy document combining algorithm
and system variables referenced from within the policies are defined for the PDP. Just use the following properties to override
the defaults:
```properties
io.sapl.pdp-config-type=filesystem
io.sapl.filesystem.config-path=/absolute/path/to/config/directory
```

To start the demo application directly from within the IDE, just start the Spring-Boot application
`org.demo.DemoApplication`, open your browser and navigate to the URL `http://localhost:8080/demo`.

### Using the remote PDP
The demo application can also use a Policy Decision Point running on a separate server. For the purposes of this demo 
application, the PDP server from the project [sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine) can be 
used. After having downloaded the project, just start the Spring-Boot application `io.sapl.pdp.server.PDPServerApplication` 
in the sub-project `sapl-pdp-server`. The PDP will look for the policy files in the directory `~/sapl/policies`. Before the 
server is started, the policy files under `src/main/resources/remote-policies` in this demo application project must be copied 
to `~/sapl/policies`. These policies distinguish in one point of those found under `src/main/resources/policies`: they cannot 
use the Policy Information Point `org.demo.pip.PatientPIP` directly (as it is part of this demo project and therefore not known 
to the policy engine). Instead the policies have to call the `PatientPIP` via the `HTTPPolicyInformationPoint`, which is provided 
by the SAPL policy engine. Therefore a PIP server must be running, providing the functionality of the `PatientPIP` via corresponding
REST endpoints. Such a server is available in the sub-project `sapl-demo-pip-server`. As it provides patient data, which is stored 
by the demo application, it has to use the same in memory database as the demo application. This is achieved by connecting to the 
database server started by the demo application. The demo application defines an H2 database server bean as follows:
```
import org.h2.tools.Server;

@Bean(initMethod = "start", destroyMethod = "stop")
public Server h2DatabaseServer() throws SQLException {
	return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
}
```
In `applications.properties` of the demo application, the default data source created by Spring Boot's auto-configuration
are overridden as follows:
```properties
spring.datasource.url=jdbc:h2:mem:demo-db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```
In `applications.properties` of the PIP server application, this database can now be used as follows:
```properties
spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:demo-db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```
The demo application has to be started next. But for it to use the remote PDP, it must be configured in `application.properties` 
as follows:
```properties
io.sapl.pdp-type=remote
io.sapl.remote.host=localhost
io.sapl.remote.port=8443
io.sapl.remote.key=YJidgyT2mfdkbmL
io.sapl.remote.secret=Fa4zvYQdiwHZVXh
```
The PDP server of the [sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine) project uses basic authentication 
to authenticate clients. The configuration values `io.sapl.remote.key` and `io.sapl.remote.secret` must match the corresponding 
values from the configuration of the PDP server (`http.basic.auth.client-key` and `http.basic.auth.client-secret`).

Now that both the PDP server from the project [sapl-policy-engine](https://github.com/heutelbeck/sapl-policy-engine) and the demo
application providing the H2 database server have been started, the PIP-Server can be started via the Spring-Boot application 
`org.demo.PipServerApplication` from the demo sub-project `sapl-demo-pip-server`. Just make sure that it is running on a port 
other than the ones used by the demo application and the PDP server. The configured port 8081 is just fine. If you modify it, 
you also have to adjust the URLs in the remote policy files.

### The demo application
The demo application shows how a [Vaadin](https://vaadin.com/framework) web application can be integrated with
[Spring Security](https://spring.io/projects/spring-security) and the [SAPL Policy Engine](https://github.com/heutelbeck/sapl-policy-engine).
It presents the use of the reactive Policy Decision Point and the reactive Policy Enforcement Point, as well as the use of reactive
multi-subscriptions.

After the user has logged in with the password `password`, four buttons are presented on the initial page of the application. The first
two buttons `Show Patient View (session based, single subscriptions)` and `Show Patient View (session based, multi-subscriptions)` lead to the 
fictitious healthcare scenario, which is also the basis of the demo application in spring-mvc-app. It demonstrates the session-based use of the 
PDP. The reactive streams returned by the Policy Decision Points of the SAPL policy engine are subscribed to in the server session of the
application. The frontend is only updated upon new requests (polling). This applies to both simple authorization subscriptions and multi-subscriptions. The variant 
accessed via the second button uses multi-subscriptions wherever possible. The corresponding performance improvement compared to the single authorization subscriptions 
is clearly noticeable when using the remote PDP.

The buttons labeled `Show Live-Data View (reactive frontend, single subscriptions)` and `Show Live-Data View (reactive frontend, multi-subscription)`show 
a view that connects data streams (pulse rate and blood pressure data) to the authorization decision streams of the PDP. Changes in the 
authorization decisions lead to immediate update of the UI without the application having to poll. Simple time-dependent policies are used
to demonstrate this. Doctors and nurses get to see pulse rate data when the number of seconds of the current time is greater than 4.
Blood pressure data is always displayed when the number of seconds of the current time is less than 31 or greater than 35. Visitors
only see pulse rate data, and also less frequently than doctors and nurses. A time scheduler panel, which is updated every two seconds, 
demonstrates the usage of reactive authorization streams with resource filtering. Nurses and visitors see blackend schedule data for doctors.

If the PDP configuration file `pdp.json` and/or the policy files are not stored in the classpath, but in any directory of the filesystem
(`io.sapl.pdp-config-type=filesystem`, `io.sapl.prp-type=filesystem`, see above), it is possible in all four variants to modify the 
configuration and/or policies while the application is running. The changes have an immediate effect on the reactive variants and on the
blocking variants after a page refresh. Restarting the demo application or the PDP server is no longer necessary in either case.
