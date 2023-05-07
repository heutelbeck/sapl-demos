# SAPL-MQTT-PEP-DEMO

This demo will start an embedded HiveMQ broker secured by the SAPL-MQTT-extension. A smart home environment will be mocked and found under http://localhost:8080/.

For each status and setting in the environment there will be an individual MQTT client. The client subscribing the door lock is not allowed to receive the security states that are regularly published in the background. For this feature the SAPL-MQTT-functions library is used.

Furthermore, the heating clients display the basic working functionality. The security camera client illustrates an example of constraint usage (blackening the payload).