# SAPL-MQTT-PEP-DEMO

This demo will start an embedded HiveMQ broker secured by the SAPL-MQTT-extension. A smart home environment will be mocked and found under http://localhost:8080/.

The smart home environment shows different states for the status attributes and allows defining different settings for the different devices in the smart home:

Behind the heating status attribute is an MQTT client which subscribes successfully the MQTT topic "heating_status". On the counterpart, when setting the heating, the MQTT client successfully publishes the ticked check-box state under the topic "heating_status". The heating status client will receive the published MQTT message and display the new heating status state in the smart home environment view. This interplay illustrates a successfully permitting behaviour of the SAPL-MQTT-extension.

The same interplay happens between the security camera status und the security camera setting where both attributes are linked with an MQTT client. The MQTT client successfully subscribes for the security camera status to the MQTT topic "security_cam_status", but because there is no security camera status published initially the security camera status will be unknown. For the status to change the user need to specify a security camera setting. The MQTT client linked with that setting attribute will then publish "on" or "off" under the topic "security_cam_status". The SAPL-MQTT-extension will permit this MQTT action but also enforce the following obligation:

    {
    "type" : "blackenPayload",
    "replacement" : "*"
    }

The security camera status will then only be ** or ***, blackened through the specified constraint.

Furthermore, the MQTT client is successfully publishing in the background door lock status events ("open", "closed") under the topic "door_lock_status/main" every five seconds. The door lock status client tries to subscribe to the topic "door_lock_status/#" which includes a MQTT wildcard. If the door lock status client would be permitted to subscribe with that wildcard we would see a change in the door lock status in the view. However, with the use of the SAPL-MQTT-functions library the subscription gets denied. This demonstrates that the SAPL-MQTT-functions library function "isMatchingAtLeastOneTopic(resource.topic, "door_lock_status")" successfully identifies the topic "door_lock_status/main" belonging to the upper level topic "door_lock_status".