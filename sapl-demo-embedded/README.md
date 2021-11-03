# Using an Embedded SAPL Policy Decision Point

## Overview

The core of a system using Attribute Stream Based Access Control (ASBAC) is the so-called Policy Decision Point (PDP).

In this demo, we will use an embedded PDP loaded from a dependency directly into an application without any 
infrastructure or framework support like Spring Boot. 

## Running the Demo

For running the demo, a local install of JDK 11 or newer and Maven are required.

First build the demo, by changing into the `sapl-demo-embedded` folder and execute the command:

```
mvn install
```

After the build completes, the `target` folder contains the executable JAR.
Change into this folder and execute the following command to run the demo:

```
java -jar sapl-demo-embedded-2.0.0-SNAPSHOT-jar-with-dependencies.jar
```

The demo accepts the following command line parameters:

```
  -f, --filesystem    If set, policies and PDP configuration are loaded from
                        the filesystem instead from the bundled resources. Set
                        path with -p.
  -h, --help          Show this help message and exit.
  -p, --path=<path>   Sets the path for looking up policies and PDP
                        configuration if the -f parameter is set. Defaults to
                        '~/sapl/policies'
  -V, --version       Print version information and exit.
```

The demo will now output some messages on the console documenting the progress of the demo process, 
together with some explanations of what is happening:

```
[INFO] Loading the PDP configuration from bundled resources: '/policies'
[INFO] reading config from jar jar:file:/C:/devkit/git/sapl-demos/sapl-demo-embedded/target/sapl-demo-embedded-2.0.0-SNAPSHOT-jar-with-dependencies.jar!/policies
[INFO] Loading a static set of policies from the bundled ressources
[INFO] load SAPL document: policies/policy_1.sapl
[INFO] load SAPL document: policies/policy_2.sapl
[INFO]
[INFO] Demo Part 1: Accessing the PDP in a blocking manner using .blockFirst()
[INFO] Decision for action 'read' : DENY
[INFO] Decision for action 'write': PERMIT
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 2: Accessing the PDP in a reactive manner using .take(1).subscribe()
[INFO] Single reactive decision by using .take(1) and .subscribe()...
[INFO] Decision for action 'read': DENY
[INFO] Decision for action 'write': PERMIT
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 3: Perform a small benchmark for blocking decisions.
[INFO] Warming up for 20000 runs...
[INFO] Measure time for 20000 runs...
[INFO]
[INFO] Benchmark results for blocking PDP access:
[INFO] Runs  : 20000
[INFO] Total : 1,6125 s
[INFO] Avg.  : 0,0806 ms
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] Demo Part 4: Perform a small benchmark for sequential .take(1) decisions.
[INFO] Warming up for 20000 runs...
[INFO] Measure time for 20000 runs...
[INFO]
[INFO] Benchmark results for .take(1) access:
[INFO] Runs  : 20000
[INFO] Total : 1,5089 s
[INFO] Avg.  : 0,0754 ms
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] End of demo.
```
