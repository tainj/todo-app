## Running the application

1. Copy the environment file and fill in your values:
```bash
cp .env.example .env
```

2. Start infrastructure (PostgreSQL, Kafka, Redis):
```bash
docker compose -f docker/docker-compose.yml up -d
```

3. Run services (each in a separate terminal):

### todo-service
```bash
cd todo-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

### notification-service
```bash
cd notification-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

    ### scheduler-service
    ```bash
cd scheduler-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

## Stopping
```bash
docker compose -f docker/docker-compose.yml down
```