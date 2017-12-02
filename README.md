# Ticket Service
---
#### Implementation of a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats.

## Assumption:
 * You can hold/reserve maximum 10 seats at a time.
 * Seat numbers are available. (e.g A1 A2 C3 Z9)
 * Seats are divided into rows from A-Z and each row has 10 seats. This can be changed easily.
 * So which are the best seats? I'm assuming Row A seats are the best seats as it's closest to the Stage.
 * Row A seats would be allocated first if available adjacent for a group hold. If not, then allocate seats from row B.
 * Total capacity is 26*(Maximum seats in each row, which is set to 10)=260 Seats.
 * Everything is stored in-memory.
 * Seats hold would be valid only for 30secs. (It's configurable)
 * Seats will be allocated in sequence if available. 
 * Rows will be filled 25% first and will be filled 100% after the venue is at 50% capacity.
 * If seats are not available together, service will allocate seats wherever its available.
 * Not a web service but can be easily converted into REST APIS using Spring boot.
 * You can find Integration Tests under /src/test/java/integration/test

--- 
## Build Project By Maven

Execute following command to build the project. This will take couple of sec to build.

```
$ cd ticket-service
```

Following command will build the project as well run the Test cases

```
$ mvn clean package
```

## Test using command line tool
Execute following command to test the service and follow the interactive Prompt 
```
$ cd target
```

```
$ java -jar ticket-service-1.0.jar
```

