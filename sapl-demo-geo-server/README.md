# Submodule SAPL Demo Geo Server

The SAPL Demo Geo Server implements a Spring RESTful server with the configuration necessary to run the SAPL Geo demonstration. The demo scenario sets place in the aviation industry, where a flight attendant wants to access passenger related information (the so called *Passenger Information List*, PIL) on her personal mobile device. This data can be retrieved through a `GET` request on this server. The client therefore has to authenticate himself with HTTP basic authentication. Afterwards the request is intercepted by a `Policy Enforcement Filter` (`PEF`) acting as the `Policy Enforcement Point` (`PEP`) in the security filter chain. The `PEF` forwards the request to an embedded SAPL `PDP`, which makes a decision based upon the respective predefined policies. This setup is made analogously to the [filterchain-demo](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-filterchain). In case authorization is granted the server also creates a random PIL and sends it back to the requesting unit (i.e. the Android app).

An example for such an Android app to access the server can be found [here](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-geo-app).

A video explaining the scenario and policy logic can be found [here](http://youtube.com).


## Installation

Configuration of the RESTful Server is usually not necessary. However, due to the complexity of the scenario, a few databases and services have to be installed on the server itself. For this purpose a docker script was implemented which can be found [here](http://docker.com). It basically does the following things:
* Opens the port for the app/server connection
* Installs and configures a Traccar server
* Installs and configures a PostGIS DB and initializes it with some basic data

## Sample Request
The following shows a sample request to the SAPL Demo Geo Server. The standard username is `a12345` with the password `password`.

```java
http://[SERVER-URL]:5699/pil?dep=FRA&dest=MIA&fltNo=LH123&date=12051990&classification=0
```
The parameters are the following:
- `dep`		IATA code of airport of departure
- `dest`	IATA code of airport of destination
- `fltNo`	Flight number
- `date`	Date of flight
- `classification` 	Access level (0: meta, 1: restricted, 2: confidential), see video above for an explanation

