# Todoku

A small Java desktop TODO application with a Swing UI, backed by a MySQL database.

Features
- User registration and login (passwords stored/compared in plain text in current implementation)
- Task CRUD (create, read/list, update, delete)
- Simple Swing UI with dialog-based error handling
- DAO + Service layers and explicit mapping from snake_case SQL columns to camelCase Java fields

Project structure (important files)
- `src/main/java/org/ilmi/model/` - domain models (`User`, `Task`) with `fromResultSet` mappers
- `src/main/java/org/ilmi/database/` - `TodokuDatabase`, `UserDAO`, `TaskDAO`
- `src/main/java/org/ilmi/service/` - `AuthenticationService`, `TaskService`
- `src/main/java/org/ilmi/ui/` - Swing UI (`LoginForm`, `RegisterForm`, `MainFrame`, `TaskDialog`)
- `src/main/resources/db/migration/V1__init-table.sql` - database schema (users + tasks)
- `src/main/java/org/ilmi/Main.java` - application launcher (starts Swing UI)

Requirements
- Java 17 or newer (JDK) installed
- Maven (for building) — optional if you run from IDE
- MySQL server (or compatible) and JDBC driver on the classpath

Database setup
1. Create a database (example name used in project): `todoku-db`.
2. Apply the SQL migration located at `src/main/resources/db/migration/V1__init-table.sql` to create `users` and `tasks` tables.

The default connection settings used by `TodokuDatabase` (edit the file if you need different values):
- URL: `jdbc:mysql://localhost:3306/todoku-db`
- Username: `root`
- Password: `example-root-pw`

If you change DB credentials or host, update `TodokuDatabase` accordingly.

Build & run

From your IDE (recommended):
- Open the project in IntelliJ IDEA (or another Java IDE)
- Ensure project SDK is set (Java 17+)
- Build and run the `org.ilmi.Main` class to launch the Swing application

From the command line (with Maven)

1) Build the project (requires Maven installed):

```sh
mvn -DskipTests package
```

2) Run the application (run Main class). If you have an executable jar configured in the `pom.xml`, run:

```sh
java -jar target/todoku-1.0-SNAPSHOT.jar
```

If there is not an executable fat jar, run the Main class from the compiled classes (example):

```sh
java -cp target/classes;path\to\mysql-connector-java.jar org.ilmi.Main
```

(Windows `;` classpath separator is used above; on *nix use `:`.)

Notes about JDBC driver
- Ensure `mysql-connector-java` is available to the runtime. Add it to your `pom.xml` dependencies or place the JAR on the classpath when running from the command line.

How the mapping from SQL to Java works
- The `User` and `Task` model classes include a static `fromResultSet(ResultSet rs)` method that explicitly reads snake_case SQL columns (for example `rs.getInt("user_id")`) and sets the corresponding camelCase Java fields (for example `setUserId(...)`).
- This explicit mapping avoids reflection and keeps type conversions (DATE/TIMESTAMP -> `LocalDate`/`LocalDateTime`) clear and maintainable.

Security note
- Passwords are currently stored and compared in plain text per your request. This is insecure for real-world usage. To harden security later, add BCrypt (e.g., `spring-security-crypto` or `jBCrypt`) and hash passwords on register, compare hash on login.

UI walkthrough
- Launch the app to see the Login form.
- Register a new user via the Register form (username, email, password). After successful registration you'll be redirected to Login.
- Login will open the Main window where you can:
  - Create Task: opens a dialog to set title (required), description, status and due date (format `yyyy-MM-dd`).
  - Edit Task: select a task row and click Edit to modify values.
  - Delete Task: select a task row and click Delete (confirmation dialog shown).
- All errors and exceptions are shown via dialog boxes.

Troubleshooting
- "Driver not found" or DB connection errors: ensure `mysql-connector-java` is on the classpath and the DB credentials in `TodokuDatabase` are correct.
- If Maven is not available on your machine, run the app from your IDE directly.
- If the `users` or `tasks` tables are missing, run the SQL in `src/main/resources/db/migration/V1__init-table.sql` against your database.

Developer notes & next steps
- Add `findByEmail` to `UserDAO` for efficient uniqueness checks in `AuthenticationService.register`.
- Replace plain-text passwords with bcrypt hashing.
- Add unit and integration tests (use H2 in-memory DB for DAO/service tests).
- Consider using a lightweight ORM (JDBI/MyBatis/JPA) if the project grows.

Contact / support
- If you want, I can:
  - Add password hashing to registration/login flows.
  - Add automated tests (JUnit + H2) and CI configuration.
  - Package an executable fat jar including the JDBC driver.

Enjoy using Todoku — tell me which next improvement you'd like me to implement and I will add it.
