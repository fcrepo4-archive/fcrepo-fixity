# fcrepo-fixity

This service is meant to be run as a client to [Fedora 4](https://github.com/futures/fcrepo4).
It is designed to run fixity checks (e.g. checksum comparison) of stored Datastreams.

## Running the service

The service is built as a Java war file and can be run directly from maven using:

```bash
$ cd fcrepo-fixity-webapp
$ MAVEN_OPTS="-Xmx512m" mvn -Djetty.port=8180 jetty:run
```

Requesting the check of an object can be done using a HTTP GET request to:

	GET http://localhost:8180/fixity/queue/<pid>

To retrieve test results of an object you can query:

	GET http://localhost:8180/fixity/<pid>

The java property `org.fcrepo.fixity.fcrepo.url` can be set to overrride the fedora4 url in the spring configuration:

	MAVEN_OPTS="-Dorg.fcrepo.fixity.fcrepo.url=http://myfedora:80/fedora"

The Java property `org.fcrepo.fixity.brokerurl` can be set to overrride the default broker service url. The default value is `tcp://localhost:61616`. If you don't have a JMS broker running you can have activemq create a runtime broker for you:

	MAVEN_OPTS="-Dorg.fcrepo.fixity.brokerurl=vm://fixitybroker"

## JSON/XML Endpoints

The webapp supports the following operations:

### Get the first 50 fixity results

	curl "http://localhost:8081/rest/results"

### Get fixity results by offset and length
	
	curl "http://localhost:8081/rest/results/0/10"

### Get a specific fixity result

	curl "http://localhost:8081/rest/results/testobj-1"

### Get general fixity statistics

	curl "http://localhost:8081/rest/results/statistics/general"

### Get daily fixity statistics for the last 30 days

	curl "http://localhost:8081/rest/results/statistics/daily"

### Get fixity details for a specific fixity check

	curl "http://localhost:8081/rest/results/details/1"

### Queue a pid for a fixity check (hacky via url param atm)

	curl -X POST "http://localhost:8081/rest/results/queue?pid=testobj-1"
	
	
