# SAPL Vaadin Demo

An example [Vaadin Flow](https://vaadin.com/flow) app demonstrating the use of the sapl-vaadin library.
Multiple pages demonstrate different use cases of the library:

- Single- and Multisubscription Page -> Changing button attributes using single and multisubscription
- Lit Templates Page -> sapl-vaadin components when using lit templates
- Constraint Handling Page -> Basic constraint handling with global and local handlers
- Annotation Page -> Reroutes all users except when user is "admin" using annotations.
- Admin Page -> Accessible only when user is "admin"

The SAPL policies used for the different pages are located at src/main/resources/policies/*.sapl

## Requirements
- Java 11

## Running the app

<!--
Uncomment when demo is put into official repository, also check if in "snapshot" or "releases":
Download the latest build from [here](https://s01.oss.sonatype.org/content/repositories/releases/io/sapl/sapl-demo-vaadin/).
-->

1) Start the app by running the Spring Boot `VaadinDemoApplication.java` file, or from the command line with the command

```
mvn spring-boot:run
```

2) Connect to localhost:8080
3) Choose a user ("user" or "admin") to login
