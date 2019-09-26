# PIP Server for the Demo-Application in `sapl-demo-reactive`

This sub-project provides a Spring-Boot application starting a server which provides REST endpoints for the PatientPIP  
and a time ticker PIP. The server is needed when the reactive demo application is configured to connect to a remote PDP. 
The PDP server then has to read policies which use the HTTP-PIP to connect to remote PIPs. These remote PIPs are provided 
by this application's server.

The server can be configured using the `application.properties` file under `src/main/resources`.

The following properties configure the port used by this server. It must be different from the port used by the demo 
application's server:
```properties
server.port=8081
```

This PIP server needs to use the same in memory database as the demo application using this PIP server (they both access 
patient data). The demo application therefore needs to start it's own H2 database server on port 9090 and accessible by 
this PIP server. The following properties configure the H2 to be used by this server:
```properties
spring.datasource.url=jdbc:h2:tcp://localhost:9090/mem:demo-db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

As the H2 server must be up and running when this PIP server is started, the demo-application has to be started before
this PIP server application.
