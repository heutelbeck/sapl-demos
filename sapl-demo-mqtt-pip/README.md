# SAPL-MQTT-PIP-DEMO

## Setup

The demo will start a mqtt broker under 'localhost:1883'. To sent messages to this server you can use a 
mqtt client of your choice, for example the open source 'MQTT.fx' client (Version 1.7.1).

## Usage

In this demo the pip subscribes messages of topics "single_topic", "multiple_topic_1" and "multiple_topic_2".
The returned message payload will be compared with the policy variable 'message' by the pdp and therefore determine
the policy evaluation result.

If you use your mqtt client and publish messages under these topics you will see different evaluation results
of the policies. 