# Municipal Civic Portal - Java 17 Backend Service

This is the Java 17 backend service for the Municipal Civic Portal, built using **Spring Boot 3.2** and **Spring Data JPA**.

## Prerequisites
- **JDK 17 or higher** installed on your system.
- An IDE (such as **VS Code** with the Extension Pack for Java, **IntelliJ IDEA**, or **Eclipse**) is recommended to run the project without needing Maven installed globally.

## Database Configurations
By default, the application is configured to run in a beginner-friendly **local File Mode** using an H2 database.
- Database file path: `./data/municipal_db` (persists on disk across server restarts).
- Web Console: When the application is running, you can visually inspect your database by opening your browser to:
  `http://localhost:5000/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/municipal_db`
  - Username: `sa`
  - Password: `password`

### Connecting to AWS RDS (PostgreSQL)
If your mentor wants to link this to a live AWS RDS database:
1. Open the [application.properties](src/main/resources/application.properties) file.
2. Comment out the H2 settings (lines 8-12).
3. Uncomment the AWS RDS PostgreSQL settings (lines 20-24) and input your RDS endpoint, username, and password.
4. On startup, Spring Data JPA will automatically scan the Entity classes and run migrations to create the required SQL tables.

## How to Run the Application

### Option A: Using your IDE (Recommended)
1. Open the `backend-java` folder in your IDE (e.g., VS Code or IntelliJ).
2. Wait for the Java Language Server to import the Maven dependencies.
3. Open [PortalApplication.java](src/main/java/com/municipal/portal/PortalApplication.java) and click **Run** or **Debug**.

### Option B: Using Command Line (If Maven is installed)
Run the following command in the `backend-java` directory:
```bash
mvn spring-boot:run
```

The service will boot up and start listening for frontend Angular API calls on `http://localhost:5000`.
