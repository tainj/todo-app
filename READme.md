## Запуск PostgreSQL

Из корня проекта (подготовьте переменные окружения):

    cp .env.example .env
    docker compose -f docker/docker-compose.yml up -d

## Остановка

```bash
docker compose -f docker/docker-compose.yml down
```