# SAPL Playground

This module is a web application to discover, learn, write and test SAPL policies.

Its UI consists out of 
- a SAPL Policy Editor on the left side (using the module ["sapl-demo-for-vaadin"](https://github.com/heutelbeck/sapl-server/tree/main/sapl-editor-for-vaadin))
- two JSON editors on the upper right side to input your AuthorizationSubscription and a definition of Mocks
- and the resulting  output on the lower left side

The playground can be run locally via maven or by executing the JAR. 
Alternatively, a container image is available.

This playground uses [Vaadin](https://vaadin.com/)

## Local Execution

### Running the Server from Source

Vaadin uses Spring Boot, so you can start the playground via

```shell
mvn spring-boot:run
```

### Running the Docker Image

To run the server locally for testing in an environment like Docker Desktop, you can run the current image as follows:

```shell
docker run -d --name sapl-demo-playground -p 8080:8080 ghcr.io/heutelbeck/sapl-demo-playground:2.0.0-SNAPSHOT-8
```