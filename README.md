# FJSSTT Web Service
This project will implement a web service for the FJSSTT Algorithm

## Requirements

- JDK1.8
- Maven3

## Start Service

    mvn clean install tomcat7:run

## Test Service

    # retrieve a dummy resource as text/plain
    curl http://localhost:8080/fjsstt-service/rest/myresource
    # calls a dummy service with query param
    curl http://localhost:8080/fjsstt-service/rest/myservice/method1?uri=asdf
    # post data to service
    curl -H 'Content-Type: application/json' \
         -d '{"uri":"asdf","name":"mybean","myField":"mydata","myList":["entry1","entry2"],"myMap":{"1":1.1,"2":1.2}}' \
         http://localhost:8080/fjsstt-service/rest/myservice/method1