
# Crash Dump

This is a high perfomant Spring boot reactive web application meant to serve hundreds(upto few thousands) of requests per second per application instance.

# Prerequisite
Your System should be having below things already installed.

    1. JVM
    2. Docker Desktop
    3. Maven
    4. Any IDE would work. (Intellij Idea Recommended)



## Packages/Dependencies

- Spring WebFlux
- Kafka
- Reactive Redis
- Docker

## Endpoints

- POST /api/collect
- GET  /api/report/total
- GET  /api/report/affected-users
## Installation

1. If you have not yet checked out this project then please do by below command

```bash
  git clone yet to come
```
2. You need to build the project now by running below command. This step will generate a Jar file of this application which will be helpful in next steps.
```bash
  mvn clean install
```
3. Now you need to run docker desktop in your computer.
4. Once Docker desktop is running, Run the below command to start the application.

```bash
  docker-compose up --build
```

You should see your application is being served on port number 8080 in the logs. Along with the spring-boot app, there are three more containers running.

    1. Zookeeper
    2. Kafka
    3. Redis

# Great your APP is up now!
You can go to below URL to play around with the Swagger UI.
```bash
  http://localhost:8080/api/webjars/swagger-ui/index.html
```
## Demo

Below are sample request which you can use to play with the API.

1.
POST /api/collect

Request body(Mime type: application/json):
```
    [
  {
    "userId": "Josh",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Null Pointer"
  },
  {
    "userId": "John",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Null Pointer"
  },
  {
    "userId": "John",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Index Out of Bound"
  },
  {
    "userId": "Rashmi",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Null Pointer"
  },
  {
    "userId": "Lydia",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "File Not Found"
  },
  {
    "userId": "Dawid",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "File Not Found"
  },
  {
    "userId": "Josh",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "File Not Found"
  },
  {
    "userId": "Ram",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Index Out of Bound"
  },
  {
    "userId": "Kayle",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "Not Found"
  },
  {
    "userId": "Lager",
    "timestamp": "String (ISO Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX)",
    "errorMessage": "File Not Found"
  }
]
```

2.

GET /api/report/total

3.
GET /api/report/affected-users