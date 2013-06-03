# fcrepo-fixity

This service is meant to be run as a client to [Fedora 4](https://github.com/futures/fcrepo4).
It is designed to run fixity checks (e.g. checksum comparison) of stored Datastreams.

## Running the service

The service is built as a Java war file and can be deployed in a ServletContainer e.g. Tomcat

Requesting the check of an object can be done using a HTTP GET request to:

	GET http://localhost:8180/fixity/fixity-result/queue/<pid>

To retrieve test results of an object you can query:

	GET http://localhost:8180/fixity/fixity-result/<pid>

The java property `org.fcrepo.fixity.fcrepo.url` can be set to overrride the fedora4 url in the spring configuration:

	MAVEN_OPTS="-Dorg.fcrepo.fixity.fcrepo.url=http://myfedora:80/fedora"

## JSON/XML Endpoints

The webapp supports the following operations, and the fixity service will by default return JSON in order to request XML content from the fixity service `-H "Accept:application/xml"` can be used as an argument for cURL

### Get the first 50 fixity results

	curl "http://localhost:8180/fixity/fixity-results/"

### Get fixity results by offset and length
	
	curl "http://localhost:8180/fixity/fixity-results/0/10"

### Get a specific fixity result

	curl "http://localhost:8180/fixity/fixity-results/testobj-1"

### Get general fixity statistics

	curl "http://localhost:8180/fixity/fixity-results/statistics/general"

### Get daily fixity statistics for the last 30 days

	curl "http://localhost:8180/fixity/fixity-results/statistics/daily"

### Get fixity details for a specific fixity check

	curl "http://localhost:8180/fixity/fixity-results/details/1"

### Queue a pid for a fixity check (hacky via url param atm)

	curl -X POST "http://localhost:8180/fixity/fixity-results/queue?pid=testobj-1"
	
	
