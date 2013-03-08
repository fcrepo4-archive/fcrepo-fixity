ff-fixity-service
=================

This service is meant to be run as a client to [Fedora 4](https://github.com/futures/fcrepo4).
It is designed to run fixity checks (e.g. checksum comparison) of stored Datastreams.

Running the service
-------------------

The service is built as a Java war file and can be run directly from maven using:
	
	mvn jetty.port=8180 clean package  jetty:run

Requesting the check of an object can be done using a HTTP GET request to:

	GET http://localhost:8180/fixity/queue/<pid>

To retrieve test results of an object you can query:

	GET http://localhost:8180/fixity/<pid>
	  	
