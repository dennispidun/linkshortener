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

## Production readiness

This repository provides an application which can be run on anything and is made almost ready for the cloud (eg.: AWS EKS/Fargate, Kubernetes).
For that reason we included spring actuactor to help setting things up.
It adds some convinience functionalities like the health-endpoints needed to properly deploy to a container-orchestration service like fargate.

To be fully production ready one would consider some more steps which can be added in the future:
- restrict endpoint exposure: Use the `management.endpoints.web.exposure.include` property to explicitly allow only necessary endpoints (e.g., /health, /info). Similar effects could be achieved with an reverse-proxy/api gateway setup in-front of the application. Exposing only whats needed is essential to determine before deploying it. That way, the orchestrator can see the relevant information, the outside world not.
- restrict access: currently everyone can interact with everything. There is also no content filter included. A production ready system would go beyond and also focus on edge-cases.
- Spring boot is fully configurable, and this application does not hinder anyone to configure it for their needs. Eg., therefore in a production ready system we would also configure things like connection settings for the database, etc.
- Centralized logging formats. We do not include logging yet, the application provides the core services. For production readiness its mandatory to have proper logging, to be also able to monitor it.
- Monitoring needs additional metrics. One can add those metrics to build application context aware alarms.
- ...

Those requirements among other would determine the production readiness state of the underlying application.
To keep things small and straightforward we do not provide further implementation details yet.

