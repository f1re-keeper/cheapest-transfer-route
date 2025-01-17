# Cheapest Transfer Route
This Spring Boot application finds the combination of transfers for a given route that has the maximal total cost while ensuring that the total package weight stays within a given limit.
The application uses the Knapsack Algorithm.

Made by Elene Kvitsiani

## Table of Contents
1. [Project Description](#project-description)
2. [Setup](#setup)
3. [API Endpoints](#api-endpoints)
4. [Testing](#testing)
5. [CURL Requests](#curl-requests)

## Setup

### Clone the GitHub Repository
```bash
git clone <repo-url>
cd <folder-name>
```
### Build the applicaiton
Keep in mind that this application starts on `http://localhost:8000`. If you would like to change that then change the port in `src/main/resources/application.properties`.
The application is configured using Maven, therefore we need to run maven:
```
mvn clean validate install
```
To start the application you need to run:
```
mvn spring-boot:run
```
### (Optional, but recommended) Postman
To run the application with Postman, write `http://localhost:8000/<endpoint>` in the address bar. If it is a POST request, then go to `Body` and choose `raw` and `JSON` for file types.
With this, you can directly input any request you want and you would not have to change the `src/main/resources/data.json` file every single time.
Using Postman won't interfere with the existence of the `data.json` file, because the application is configured so that every new request overwrites the `data.json` file.

## **API Endpoints**

###  GET: /api/getBestRoute
Gives the solution to the given problem:
**Example:**
```
{
"selectedTransfers": [
{
"weight": 5,
"cost": 10
},
{
"weight": 10,
"cost": 20
}
],
"totalCost": 30,
"totalWeight": 15
}
```
**Response:**
```
200 OK
```

### POST: /api/requestInput
Sends the properties of the problem
**Example:**
```
{
  "maxWeight": 15,
  "availableTransfers":
  [
    {
      "weight": 5,
      "cost": 10
    },
    {
    "weight": 10,
    "cost": 20
    },
    {
    "weight": 3,
    "cost": 5
    },
    {
    "weight": 8,
    "cost": 15
    }
  ]
}
```
**Response for valid input:**
```
HTTP/1.1 200 OK
```
**Response for invalid input:**
```
HTTP/1.1 400 BAD REQUEST
```

## Testing
You can see the tests in `src/test/java/org.example.cheapesttransferroute`. These tests also use custom JSON files located in `src/test/java/jsonFiles`. Run the tests with:
```
mvn test
```

## CURL Requests

### GET
```
curl -i -X GET http://localhost:8000/api/getBestRoute
```
Omit `i` if you don't want response headers

### POST
```
curl -i -X POST http://localhost:8000/api/requestInput \-H "Content-Type: application/json" -d \
'{
  "maxWeight": 15,
  "availableTransfers":
  [
    {
      "weight": 5,
      "cost": 10
    },
    {
    "weight": 10,
    "cost": 20
    },
    {
    "weight": 3,
    "cost": 5
    },
    {
    "weight": 8,
    "cost": 15
    }
  ]
}'
```



