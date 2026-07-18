# Municipal Civic Portal - Java 17 Backend Service

This is the Java 17 backend service for the Municipal Civic Portal, built using **Spring Boot 3.2** and **Spring Data JPA**.

## Prerequisites
- **JDK 17 or higher** installed on your system.
  - Recommended environment variable configuration: `JAVA_HOME` pointing to your local JDK directory (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot`).
- An IDE (such as **VS Code** with the Extension Pack for Java, **IntelliJ IDEA**, or **Eclipse**).

## Database Configurations
The application is configured to use a local **MySQL Database Server**.
- **Database Name**: The application automatically connects to your local instance and maps to your municipal schema.
- **Table Generation**: On initial startup, Spring Data JPA automatically drops and recreates the required tables (`users`, `complaints`, `permits`, `taxes`) based on the entity definitions.
- **Authentication**: Connects using standard security protocols (`username` and `password` configurations declared within `application.properties`).

## How to Run the Application

### Using Command Line (Recommended Terminal Run)
To ensure all code modifications—including CORS policy and endpoint updates—are applied properly, execute a clean build from the `backend-java` root directory:

```bash
..\maven\apache-maven-3.9.6\bin\mvn clean spring-boot:run
