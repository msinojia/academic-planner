# Academic Planner

Academic planner is a calendar application built for the needs of university students. The application builds the perfect schedule for students.

## Dependencies

Backend dependencies:

- [Spring Boot Starter Web v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/2.6.3)
- [Spring Boot Starter Data JPA v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/2.6.3)
- [MySQL Connector/J v8.0.32](https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.32)
- [Spring Boot Starter Test v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test/2.6.3)
- [Spring Boot Starter Validation v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation/2.6.3)
- [jBCrypt v0.4](https://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.4)
- [Spring Boot Starter Mail v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-mail/2.6.3)
- [Lombok v1.18.26](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.26)
- [ModelMapper v2.4.4](https://mvnrepository.com/artifact/org.modelmapper/modelmapper/2.4.4)
- [Apache Commons Lang v3.12.0](https://mvnrepository.com/artifact/org.apache.commons/commons-lang3/3.12.0)
- [Spring Boot Starter Security v2.6.3](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security/2.6.3)
- [Java JWT v0.11.2](https://mvnrepository.com/artifact/com.auth0/java-jwt/0.11.2)
- [Jackson Databind v2.13.1](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.13.1)

Frontend

- [antd](https://ant.design/)
- [axios](https://axios-http.com/)
- [dayjs](https://day.js.org/)
- [react](https://reactjs.org/)
- [react-big-calendar](https://github.com/jquense/react-big-calendar)
- [react-dom](https://reactjs.org/docs/react-dom.html)
- [react-redux](https://react-redux.js.org/)
- [react-router-dom](https://reactrouter.com/web/guides/quick-start)
- [react-scripts](https://create-react-app.dev/docs/getting-started/)
- [redux](https://redux.js.org/)
- [web-vitals](https://web.dev/vitals/)


## Build Documentation

Follow these steps to build and run the project:

1. Clone the repository:
```
git clone https://git.cs.dal.ca/courses/2023-winter/csci-5308/group13.git
```

2. Navigate to the project directory:
```
cd group13
```

3. Build the project:
```
mvn clean install
```

4. Run the project:
```
java -jar target/academic-planner-backend-0.0.1-SNAPSHOT.jar
```

To deploy the project to a server, follow these steps:

1. Copy the `academic-planner-backend-0.0.1-SNAPSHOT.jar` file from the `target` directory to your server.

2. On the server, run the following command:
```
java -jar target/academic-planner-backend-0.0.1-SNAPSHOT.jar
```


## User Scenarios

__Feature 1: User authentication__

User can register an account. Then he needs to verify his email. Post that user can log in to his account.

__Feature 2: Set profile__

When the user logs in for the first time, they setup their profile by adding timings for their classes, part times and leisure activities. User has an option to add either one-time events or repeating events with daily or weekly repetition.

__Feature 3: Add events to be scheduled__

User can add events with their deadline and the duration of time that they expect to spend on the event. Upon creation, the application schedules the event at a specific time.

__Feature 4: Mark scheduled event as done__

User can mark a scheduled event as done and it will be removed from calendar view.

__Feature 5: Update/Delete events__

User can update the details of the events. They can also delete the events.

__Feature 6: Reschedule events__

User can click on reschedule button to reschedule events. If some events could not be scheduled then the user would be notified.