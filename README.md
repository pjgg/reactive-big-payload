# reactive-big-payload Project

Scenario that reproduce some errors on the logs when we are working with heavy payloads

## With compression

`mvn clean verify`

Works without errors. 

## Without compression

`mvn clean verify -Dquarkus.http.enable-compression=false`

Works but there are some errors on the logs. 