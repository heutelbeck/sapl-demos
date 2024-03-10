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

If the image is not available locally, download it from [GitHub](https://github.com/users/heutelbeck/packages/container/package/sapl-demo-playground). If image-pull-on-run is enabled, as is the case with Docker Desktop, the image does not need to be downloaded beforehand.

```shell
docker pull ghcr.io/heutelbeck/sapl-demo-playground:3.0.0-SNAPSHOT
```

To run the server locally, you can run the current image as follows:

```shell
docker run -d --name sapl-demo-playground -p 8080:8080 ghcr.io/heutelbeck/sapl-demo-playground:3.0.0-SNAPSHOT
```
