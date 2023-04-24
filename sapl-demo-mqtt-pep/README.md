# SAPL-MQTT-PEP-DEMO

## Setup

The demo will start a mqtt broker under 'localhost:1883'. To sent messages to this server you can use a 
mqtt client of your choice, for example the open source 'MQTT.fx' client (Version 1.7.1). Keep in mind
that if the broker enforces denies it reacts differently with different mqtt versions of mqtt clients
according to the mqtt oasis standard and implementation specifics of the HiveMQ broker.

## Demonstration

In this demo two clients with the client ids 'weather_station' and 'outdoor_thermometer' subscribe to the mqtt broker
and are successfully authorized. After that, the weather station subscribes to the topic 'temperature' and
the outdoor thermometer client publishes a message which the weather station receives. During this publish
you can see that the obligation in the 'outdoor_thermometer_policy' leads to an altering of the initially empty 
content type to 'temperature_celsius'.

At last, the weather station tries to subscribe to mqtt messages of topic 'humidity' but gets denied. The broker
will respond with the reason for the unsuccessful subscription attempt of the client.