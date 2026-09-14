# Lanka Wings Airlines — User Registration, Login & Notification

Technology: JSP + Java Servlets + JDBC + Microsoft SQL Server + CSS/JavaScript. Compatible with Tomcat 10.1+ (Jakarta Servlet 6).

## Database
1. Run `sql/LankaWingsDB.sql` in SQL Server Management Studio.
2. Run `sql/CREATE_SQL_LOGIN_Lankawings.sql` if the SQL login does not already exist.
3. Database settings are in `src/main/java/com/lankawings/config/DBConnection.java`.

## Build
Requires JDK 17+, Maven 3.9+, and Tomcat 10.1+.

```bash
mvn clean package
```

Deploy the generated WAR from `target/` to Tomcat.

## Included Function
- User registration
- Secure login and logout
- Profile management
- Password update/reset while signed in
- User role information
- Notifications and mark-all-read

## Demo Accounts
- Administrator: `admin@lankawings.com` / `Lankawings@admin`
- Passenger: `passenger` / `Passenger123`
