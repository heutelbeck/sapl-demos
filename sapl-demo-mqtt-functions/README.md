# SAPL-MQTT-FUNCTIONS-DEMO

This demo will start an embedded sapl pdp. It will be subscribed to the pdp with different mqtt topics as the authorization subscription resource. You will see how the sapl mqtt functions library is used to check whether a mqtt topic matches a mqtt wildcard topic or not. 

In a mqtt authorization context it is possible, through the design of the policy with the combining algorithm set to ```deny-overrides```, for the user to generally allow topics with wildcards except certain wildcard topics matching predefined mqtt topics.