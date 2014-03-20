Angular Android
===============

Angular Android application has been built as a proof of concept to explore 2 separate topics: 

* Messaging
* Using Angular.js for Android apps

Messaging
---------

The goal for messaging support is to build some mechanism which would allow a mobile application connect to Enterprise Service Bus (ESB) in a relayable manner. Angular Android uses the [paho java client](http://git.eclipse.org/c/paho/org.eclipse.paho.mqtt.java.git/) implementation of [MQTT](http://mqtt.org/) protocol. Additional code wraps the paho library as an Android Service and further simplifies/narrows down the API to be used with the service. 

The messaging library is built to be used to implement two way guranteed message delivery. The exposed api (see [ServiceConnection.java](https://github.com/Hill30/mqtt-client/blob/master/mqttClient/src/main/java/com/hill30/android/mqttClient/ServiceConnection.java)) provides a way both to receive messages via a callback to be provided to the ServiceConnection constructor as well as send messages using send method on the instance of the ServiceConnection. See [acitivityRecordStorage/Storage.java](https://github.com/Hill30/mqtt-client/blob/master/serviceTracker/src/main/java/com/hill30/android/serviceTracker/activityRecordStorage/Storage.java) for an example

