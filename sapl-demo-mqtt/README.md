# MQTT Attributes Demo

This demo secures the SSE endpoint http://localhost:8080/secured based on the last message on the topic "status" in an embedded MQTT broker. The status is toggled between "ok" and "emergency" every few seconds, and only while there is an emergency, access is granted and data flows to the SSE endpoint.
