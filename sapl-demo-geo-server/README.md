# Submodule SAPL Demo Geo Server

The SAPL Demo Geo Server implements a SAPL PDP with the configuration necessary to run the SAPL Geo demonstration. The demo scenario sets place in the aviation industry, where a flight attendant wants to access passenger related information (the so called *Passenger Information List*, PIL) on her personal mobile device. An example for such an Android app can be found [here](https://github.com/heutelbeck/sapl-demos/tree/master/sapl-demo-geo-app). This demo server comes with predefined policies for the scenario. In case authorization is granted it also creates a random PIL and sends it back to the requesting unit (i.e. the Android app).

A video explaining the scenario and policy logic can be found [here](http://youtube.com).


## Installation

Configuration of the PDP Server is usually not necessary. However, due to the complexity of the scenario, a few databases and services have to be installed on the server itself. For this purpose a docker script was implemented which can be found [here](http://docker.com). It basically does the following things:
* Opens the port for the app/server connection
* Installs and configures a Traccar server
* Installs and configures a PostGIS DB and initializes it with some basic data
