# Counter Retrieval Service

This microservice provides functionalities to retrieve usage data in a basic COUNTER-like format from SUSHI interfaces or from uploaded files.
Data are stored in flattened database tables.

Currently implemented are journal report 1, book reports 1 and 2 and database reports 

### Requirements
To execute this program, you need 
* Java JDK
* Maven
* Database (e.g. PostgreSQL)


## Structure
The service comprises of a Spring Batch service and several repositories. It can be deployed in a microservice architecture with Eureka detection and a config server. 
It uses three databases:
 
- The default datasource stores the SUSHI-Profiles, log-data and Spring Batch data. 
- The second one ("data") is used to store the retrieved usage data.
- The third one (Redis) is used by Spring Session, if the application is integrated into a microservice architecture.

the first two can be configured via the .properties file, either directly in the resources-folder or provided via a config server.
The properties need to contain the following lines:

```
spring.data.datasource.jdbc-url=<url default database>
spring.data.datasource.data-username=<username default database>
spring.data.datasource.password=<password default database

spring.datasource.jdbc-url=<url data database>
spring.datasource.data-username=<username data database>
spring.datasource.password=<password data database>
```

The service has been tested with two Postgresql-databases. 

## SUSHI-Services

The service is controlled via HTTP-commands. The port of the service is given by the application.properties (default: 11877).

###Sushi Provider definition

Sushiprovider are stored by POSTing the provider-data in JSON-Format like

```
{
  "identifier" : "test",
  "name" : "Test publisher",
  "sushiURL" : "http://sushi.test.com/SushiService.svc",
  "sushiRequestorID" : "398473457465487",
  "sushiRequestorName" : "Max Mustermann",
  "sushiRequestorEmail" : "max@mustermann.de",
  "sushiCustomerReferenceID" : "jkfghdkjhfdkj",
  "sushiCustomerReferenceName" : "",
  "sushiRelease" : 4,
  "reportTypes" : [ "JR1", "BR1", "BR2" ],
}
```
 
to the sushiprovider repository address:

```
<server-address>:<server-port>/sushiprovider
```

e.g when starting with the default values:

```
http://localhost:11877/sushiprovider
```


GET-request to the same address yield a HATEOAS-conform response listing all the stored providers. A simplified version containing only a JSON list of all stored providers is available under 

```
<server-address>:<server-port>/sushiprovider/all
```

Single sushiproviders can be accessed via its identifier
 
```
<server-address>:<server-port>/sushiprovider/<identifier>
```

These requests can be either made by command-line tools such as curl or from Web-Frontend such as the [Media-Frontend](https://github.com/ETspielberg/media-management)

## Collection of counter data
Once a sushi profile has been stored, corresponding counter data can be retrieved via a GET request to

```
<server-address>:<server-port>/sushi?identifier=<identifier>&mode=<mode>&year=<year>&month=<month>
```

where <identifier> represents the identifier for the sushiprovider.

<mode> can take the values "update", "full", "year" and "month"

<year> depicts the year for which counter data are requested and month the corresponding <month>. Parameters which are not needed can be ommitted:

Examples:

Collecting the latest counter data for all stored counter types for the sushiprovider "test":
```
http://localhost:11877/sushi?identifier=test&mode=update
```

Collecting all available counter data for all stored counter types for the sushiprovider "test":
```
http://localhost:11877/sushi?identifier=test&mode=full
```

Collecting the counter data for the year 2015 for all stored counter types for the sushiprovider "test":
```
http://localhost:11877/sushi?identifier=test&mode=year&year=2015
```

Collecting the counter data for the january 2017 for all stored counter types for the sushiprovider "test":
```
http://localhost:11877/sushi?identifier=test&mode=month&year=2017&month=1
```

### Scheduled collection
By default, recent counter data (like in the update case above) are collected on the 20th of each month.


## Retrieving stored counter data
Counter data collected from the providers are stored into the data-database and cen be retreived by the data repository endpoints:

```
<server-address>:<server-port>/journalcounter
```

```
<server-address>:<server-port>/ebookcounter
```

```
<server-address>:<server-port>/databasecounter
```

Special endpoints exist for getting all data connecting to a certain publisher or platform, for example for the journal counters:

```
<server-address>:<server-port>/journalcounter/getForPublisher?publisher=<publisher-name>
```

```
<server-address>:<server-port>/journalcountergetForPlatform?platform=<platform-name>
```

In addition journal counters can be retrieved using 

```
<server-address>:<server-port>/getForIssn?issn=<issn>
```

In fact, if no online ISSN is present, print ISSN, DOI and proprietary identifiers are searched. 

A similar mechanism is available for e-book counters 

```
<server-address>:<server-port>/getForIsbn?issn=<isbn>
```

In this case, if no online ISBN is present, print ISBN, DOI and proprietary identifiers are searched. 

## Reporting
to be done