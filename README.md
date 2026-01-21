InfluxDB + Spring Boot Demo

This project demonstrates how to build a Spring Boot web application that connects to InfluxDB 3 Core, queries data using SQL, and displays the results in a neatly formatted HTML table using Thymeleaf.

Features

Connects to InfluxDB 3 Core (OSS) via HTTP

Executes SQL queries using /api/v3/query_sql

Parses JSON query results into Java objects

Renders data as an HTML table (Thymeleaf)

Provides both HTML and JSON endpoints for testing

Architecture Overview

InfluxDB (port 8181) stores the data.
Spring Boot (port 8080/8081) serves web pages and calls InfluxDB.

Data flow:

InfluxDB → Spring Boot (InfluxClient) → JSON → Java objects (FoodRecord) → Thymeleaf → HTML table

Technologies Used

Java 21

Spring Boot

InfluxDB 3 Core (OSS)

Thymeleaf

Maven

VS Code

Project Structure

src/main/java/com/example/influxdemo/

InfluxdemoApplication.java (app entry point)

WebController.java (web routes)

InfluxClient.java (InfluxDB communication)

FoodRecord.java (model used for parsing rows)

src/main/resources/

templates/table.html (HTML template)

application.properties (configuration)

Configuration

Edit src/main/resources/application.properties:

spring.application.name=influxdemo
server.port=8081
influx.url=http://localhost:8181

influx.token=YOUR_INFLUXDB_TOKEN
influx.db=inflx_demo

Security note: Do not commit your real token. Use environment variables or add application.properties to .gitignore.

Running the Project
1) Start InfluxDB 3 Core

Ensure InfluxDB is running locally on port 8181.

Verify:

curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8181/health

Expected output: OK

2) Ensure database/data exists

You can verify via InfluxDB SQL:

SHOW DATABASES;
SHOW TABLES;
SELECT * FROM food LIMIT 10;

3) Run Spring Boot

From the project root:

mvn spring-boot:run

Or run using VS Code Run/Debug.

Available Endpoints
HTML Table View

http://localhost:8081/table

Displays queried InfluxDB data in a formatted HTML table.

Raw JSON API

http://localhost:8081/api/foods

Returns the raw JSON response from InfluxDB (useful for debugging).

How It Works

InfluxClient sends a SQL query to InfluxDB using HTTP POST.

InfluxDB returns query results as JSON.

WebController parses JSON into List<FoodRecord> using Jackson.

The list is passed into the Thymeleaf template.

Thymeleaf renders the list into an HTML <table>.

Common Issues
Port 8080 already in use

Either stop the process using port 8080 or change Spring Boot port:

server.port=8081

Circular view path error

Ensure:

Thymeleaf dependency is inside the <dependencies> section in pom.xml

table.html is located at src/main/resources/templates/table.html

No data showing

Verify directly in InfluxDB:

SELECT * FROM food;

If InfluxDB returns no rows, the table will be empty.

Learning Outcomes

This project demonstrates:

Spring Boot MVC routing (@GetMapping)

Connecting Java apps to external services (InfluxDB)

JSON parsing into Java objects

Server-side HTML rendering with Thymeleaf

Debugging by testing each layer independently (InfluxDB → API → HTML)

Security Notes

Never commit API tokens to Git

Use environment variables or .gitignore for secrets

Possible Extensions

Add a form to insert rows into InfluxDB

Auto-refresh the table after inserting

Format timestamps for readability

Add filtering or pagination

Add a separate frontend that consumes /api/foods

Author

Built as a learning project to understand Spring Boot + InfluxDB integration.
