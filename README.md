[![Build Status](https://github.com/heutelbeck/sapl-demos/workflows/build/badge.svg)](https://github.com/heutelbeck/sapl-demos/actions)

# SAPL Demo Projects

This project contains some demo modules demonstrating the usage of the SAPL engine.
A good point to start exploring SAPL is by running and experimenting with modifying these demo projects.

If you are interested in how SAPL would be used in an application, you should take a look at [sapl-demo-mvc-app](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-mvc-app) and [sapl-demo-webflux](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-webflux).

If you want to get familiar with using a PDP directly, start with [sapl-demo-embedded](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-embedded).

After that pick a demo that matches your interest.

* [sapl-demo-webflux](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-webflux): Demonstrates how to hook a SAPL PEP into method security with Spring Security and Webflux.


* [sapl-demo-webflux-filterchain](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-webflux-filterchain): Demonstrates how to hook a SAPL PEP into the Spring Security reactive filter chain in Webflux.


* [sapl-demo-mvc-app](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-mvc-app): A full stack Spring MVC application secured with SAPL. Demonstrates non-reactive declarative Policy Enforcement Points via annotations.


* [sapl-demo-filterchain](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-filterchain): Demonstrates how to hook a SAPL PEP into the Spring Security filter chain for non-reactive environments.


* [sapl-demo-embedded](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-embedded): Manually instantiating a SAPL Policy Decision Point (PDP) and basic PDP interaction. Reading policies from bundled resources or monitoring a file system.


* [sapl-demo-remote](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-remote): Connect to a dedicated SAPL PDP Server.

* [sapl-demo-extension](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-extension): Write a custom Policy Information Point (PIP) and function library to extend SAPL with custom attributes and functions.

* [sapl-demo-jwt](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-jwt): A resource server secured with OAuth 2.0, JSON Web Tokens (JWT) and SAPL, with a matching OAuth authorization server and client application.

* [sapl-demo-web-editor](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-web-editor): Demonstrates the Vaadin-based SAPL policy editor component.

* [sapl-demo-testing](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-testing): Demonstrates how to test SAPL policies with unit tests, including test code coverage reports.

* [sapl-demo-mqtt-pip](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-mqtt-pip): Demonstrates how to evaluate MQTT messages with a Policy Information Point.
