# LinkShortener

This is a basic spring boot + gradle application written in kotlin.

## How to run

This microservice is requiring an sql database (eg.: postgres), which is provided within our docker-compose.yml. You can run it by calling

```bash
    docker-compose up -d
```

after that we can spin up the application with the following command, which also sets the dev profile.
The dev profile is configured in `src/main/resources/application-dev.yml`.

```bash
    ./gradlew bootRun --args='--spring.profiles.active=dev'
```

The application is now accessible via: `http://localhost:8080/shortlinks`.

Swagger UI is also configured, which can be accessed under: `http://localhost:8080/swagger-ui/index.html`

