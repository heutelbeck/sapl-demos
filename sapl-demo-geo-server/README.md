# Submodule SAPL Demo Geo Server

The SAPL Demo Geo Server implements a Spring RESTful server with the configuration necessary to run the SAPL Geo demonstration. The demo scenario sets place in the aviation industry, where a flight attendant wants to access passenger related information (the so called *Passenger Information List*, PIL) on her personal mobile device. This data can be retrieved through a `GET` request on this server. The client therefore has to authenticate himself with HTTP basic authentication. Afterwards the request is intercepted by a `Policy Enforcement Filter` (`PEF`) acting as the `Policy Enforcement Point` (`PEP`) in the security filter chain. The `PEF` forwards the request to an embedded SAPL `PDP`, which makes a decision based upon the respective predefined policies. This setup is made analogously to the [filterchain-demo](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-filterchain). In case authorization is granted the server also creates a random PIL and sends it back to the requesting unit (i.e. the Android app).

An example for such an Android app to access the server can be found [here](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-geo-app).

A video explaining the scenario and policy logic can be found [here](https://www.youtube.com/watch?v=OtSotMNmX9Y).


## Installation

Configuration of the RESTful Server is usually not necessary. However, due to the complexity of the scenario, a few databases and services have to be made available. The easiest way to setup all of them is probably through [Docker](https://www.docker.com/).

* A Traccar server has to be installed, configured and for test purposes filled with some sample data.

```
docker run -d --name traccar-server -p 5000-5150:5000-5150 -p 8082:8082 traccar/traccar
```
* A PostGIS database has to be installed and initialized with some sample data. You can find a preconfigured `data`-folder to be used with your database in the *src/main/resources*-folder. Within Docker you can unpack this archieve into a volume `pg_data` and mount it to a PostGIS-database with the following command.

```
docker run --name=postgis_sapl_geo -d -e POSTGRES_USER=sapl_pip -e POSTGRES_PASS=openconjurer -e POSTGRES_DBNAME=airline_backend -e ALLOW_IP_RANGE=0.0.0.0/0 -p 5432:5432 -v pg_data:/var/lib/postgresql/data --restart=always mdillon/postgis
```
* The KML-file in *src/main/resources/trustedGeofences.kml* has to be made available either through the file system or a web server.

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
- `type` (optional) If set to "recurrent", server will just answer with the decision instead of a generated PIL
