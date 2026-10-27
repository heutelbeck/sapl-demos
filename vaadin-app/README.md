# SAPL Vaadin Application Demo

A [Vaadin Flow](https://vaadin.com/flow) application showing the policy enforcement points of the `sapl-vaadin` module.
Each page demonstrates one way of enforcing SAPL decisions in a user interface:

- **Single- and Multisubscription Page**: buttons and a text field follow streaming decisions, one subscription per component or one multi subscription for all of them
- **Constraint Handling Page**: a pizza order form validated by JSON schemas carried in obligations, a local constraint handler for the minimum order and a confirmation dialog outside the opening hours
- **Annotation Page**: navigation guarded by `@OnDenyNavigate`, users who are not admins are sent back to the home page
- **Admin Page**: the same guard built with the `PepBuilderService` lifecycle builder, admins get a notification

The policies live in `src/main/resources/policies`.

## Running the Demo

```bash
mvn spring-boot:run
```

Open http://localhost:8080 and log in as `admin` (password `admin`) or `user` (password `user`).

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-vaadin</artifactId>
</dependency>
```

The module registers its services through Spring Boot auto configuration. Navigation enforcement can be switched off with `io.sapl.vaadin.navigation.enabled=false`.

## Tests

The tests run the views without a browser using Vaadin browserless testing (`browserless-test-spring`) and Spring Security test annotations.

```bash
mvn test
```
