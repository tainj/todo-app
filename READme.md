## Running the application

1. Copy the environment file and fill in your values:
```bash
cp .env.example .env
```

2. Start PostgreSQL:
```bash
docker compose -f docker/docker-compose.yml up -d
```

3. Run the application:
```bash
export $(cat .env | xargs) && mvn spring-boot:run
```

## Stopping

```bash
docker compose -f docker/docker-compose.yml down
```