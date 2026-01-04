# SAPL Demo Projects

This repository contains demo projects demonstrating various aspects of SAPL (Streaming Attribute Policy Language) for Attribute-Based Access Control.

## Getting Started

If you are new to SAPL, start with these demos:

- [embedded-pdp](embedded-pdp) - Manually construct and use a PDP without framework support
- [webflux](webflux) - Reactive method security with streaming PEP annotations
- [queryrewriting-jpa](queryrewriting-jpa) - Row-level security with Spring Data JPA

## Demo Categories

### Core PDP Usage

| Demo                         | Description                                         |
|------------------------------|-----------------------------------------------------|
| [embedded-pdp](embedded-pdp) | Build and use a PDP programmatically without Spring |
| [remote-pdp](remote-pdp)     | Connect to a remote PDP server via HTTP or RSocket  |

### Spring Security

| Demo                                                         | Description                                             |
|--------------------------------------------------------------|---------------------------------------------------------|
| [web-mvc-app](web-mvc-app)                                   | Method-level security annotations in Spring MVC         |
| [webflux](webflux)                                           | Reactive method security with streaming PEP annotations |
| [web-authorizationmanager](web-authorizationmanager)         | Filter chain authorization in Spring MVC                |
| [webflux-authorizationmanager](webflux-authorizationmanager) | Filter chain authorization in WebFlux                   |

### Database Query Rewriting

| Demo                                                               | Description                              |
|--------------------------------------------------------------------|------------------------------------------|
| [queryrewriting-jpa](queryrewriting-jpa)                           | Row-level security with Spring Data JPA  |
| [queryrewriting-sql-reactive](queryrewriting-sql-reactive)         | Row-level security with R2DBC            |
| [queryrewriting-mongodb-reactive](queryrewriting-mongodb-reactive) | Row-level security with reactive MongoDB |

### Integrations

| Demo | Description |
|------|-------------|
| [mqtt](mqtt) | MQTT as a Policy Information Point for real-time attributes |
| [oauth2-jwt](oauth2-jwt) | OAuth 2.0 / JWT integration with SAPL policies |

### Policy Testing

| Demo | Description |
|------|-------------|
| [test-dsl-junit](test-dsl-junit) | Test policies with .sapltest files and JUnit |
| [test-dsl-programmatic](test-dsl-programmatic) | Programmatic test execution with PlainTestAdapter |
| [test-fixture](test-fixture) | Java fluent API for policy testing |

### Tools

| Demo | Description |
|------|-------------|
| [editor-components](editor-components) | Vaadin-based SAPL policy editor components |

## Prerequisites

All demos require:
- JDK 21 or newer
- Maven

Some demos have additional requirements documented in their READMEs.
