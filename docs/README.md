# Todo Application

Микросервисное приложение для управления задачами с уведомлениями через Telegram и WebSocket.

## 📋 Содержание

- [Архитектура](#архитектура)
- [Сервисы](#сервисы)
- [Быстрый старт](#быстрый-старт)
  - [Запуск через Docker Compose](#запуск-через-docker-compose)
  - [Локальная разработка](#локальная-разработка)
- [Взаимодействие сервисов](#взаимодействие-сервисов)
- [API](#api)
- [Релизы](#релизы)
- [Лицензия](#лицензия)

## 🏗️ Архитектура

Приложение построено по микросервисной архитектуре и состоит из следующих компонентов:

```
┌─────────────┐     ┌──────────────────┐     ┌────────────────────┐
│   Frontend  │────▶│      Nginx       │────▶│    Todo Service    │
│  (React/Vue)│     │   (Load Balancer)│     │   (Spring Boot)    │
└─────────────┘     └──────────────────┘     └────────────────────┘
                            │                        │
                            │                        ▼
                            │              ┌────────────────────┐
                            │              │     PostgreSQL     │
                            │              │   (Основная БД)    │
                            │              └────────────────────┘
                            │                        │
                            ▼                        ▼
                    ┌──────────────────┐     ┌────────────────────┐
                    │ Notification Svc │◀────│   Scheduler Svc    │
                    │  (Spring Boot)   │ Kafka│   (Spring Boot)   │
                    └──────────────────┘     └────────────────────┘
                            │                        │
                            ▼                        ▼
                    ┌──────────────────┐     ┌────────────────────┐
                    │    Telegram API  │     │       Redis        │
                    │   & WebSocket    │     │     (Кэш/Хран.)    │
                    └──────────────────┘     └────────────────────┘
```

### Технологический стек

- **Backend**: Java 17+, Spring Boot 3.x
- **Frontend**: TypeScript, React/Vite
- **Базы данных**: PostgreSQL 17
- **Очереди сообщений**: Apache Kafka
- **Кэширование**: Redis 7
- **Веб-сервер**: Nginx
- **Контейнеризация**: Docker, Docker Compose

## 🔧 Сервисы

### 1. Todo Service (`todo-service`)
Основной сервис для управления задачами и категориями.

**Порты**: 8080  
**Зависимости**: PostgreSQL, Redis, Kafka

**Функционал**:
- CRUD операции с задачами
- Управление категориями
- Аутентификация и авторизация пользователей
- Интеграция с Telegram

### 2. Scheduler Service (`scheduler-service`)
Сервис планировщика напоминаний.

**Порты**: 8082  
**Зависимости**: PostgreSQL, Redis, Kafka

**Функционал**:
- Планирование напоминаний
- Отправка событий уведомлений в Kafka
- Мониторинг просроченных задач

### 3. Notification Service (`notification-service`)
Сервис уведомлений.

**Порты**: 8081  
**Зависимости**: Redis, Kafka, PostgreSQL

**Функционал**:
- Обработка событий из Kafka
- Отправка уведомлений в Telegram
- WebSocket уведомления для frontend

### 4. Frontend (`front/frontend`)
Веб-интерфейс приложения.

**Технологии**: TypeScript, Vite  
**Порт**: 5173 (dev), 80 (prod через Nginx)

### 5. Nginx
Прокси-сервер для маршрутизации запросов.

**Порт**: 80

## 🚀 Быстрый старт

### Запуск через Docker Compose

Полный запуск всех сервисов в контейнерах:

```bash
# 1. Скопируйте файл окружения
cp .env.example .env

# 2. Отредактируйте .env при необходимости
# Обязательно замените TELEGRAM_BOT_TOKEN и TELEGRAM_BOT_USERNAME

# 3. Запустите все сервисы
docker compose up -d

# 4. Проверьте статус
docker compose ps

# 5. Просмотр логов
docker compose logs -f
```

Приложение будет доступно по адресу: http://localhost:80

### Локальная разработка

Для разработки отдельных сервисов без контейнеризации:

#### Предварительные требования

- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker (для инфраструктуры)

#### 1. Запуск инфраструктуры

```bash
docker compose up postgres kafka redis -d
```

#### 2. Настройка окружения

```bash
cp .env.example .env
# Отредактируйте .env под ваши нужды
```

#### 3. Запуск сервисов

**Todo Service:**
```bash
cd todo-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

**Scheduler Service:**
```bash
cd scheduler-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

**Notification Service:**
```bash
cd notification-service
export $(cat ../.env | xargs) && mvn spring-boot:run
```

**Frontend:**
```bash
cd front/frontend
pnpm install
pnpm dev
```

#### 4. Доступ к сервисам

| Сервис | URL |
|--------|-----|
| Frontend | http://localhost:5173 |
| Todo API | http://localhost:8080/api |
| Notification WS | ws://localhost:8081/ws |
| PostgreSQL | localhost:5432 |
| Kafka | localhost:9092 |
| Redis | localhost:6379 |

## 🔄 Взаимодействие сервисов

### Схема взаимодействия

```
┌──────────────┐         ┌──────────────┐         ┌─────────────────┐
│ Todo Service │────────▶│     Kafka    │────────▶│ Notification Svc│
│              │ publish │              │consume  │                 │
└──────────────┘         └──────────────┘         └─────────────────┘
       │                       │                          │
       │                       │                          ▼
       │                       │                   ┌─────────────┐
       │                       │                   │  Telegram   │
       │                       │                   │     API     │
       │                       │                   └─────────────┘
       │                       │                          │
       ▼                       ▼                          ▼
┌──────────────┐         ┌──────────────┐         ┌─────────────┐
│ PostgresSQL  │         │    Redis     │         │  Frontend   │
│   (Данные)   │         │  (Кэш/Сессии)│         │(WebSocket)  │
└──────────────┘         └──────────────┘         └─────────────┘
```

### Топики Kafka

| Топик | Производитель | Потребитель | Описание |
|-------|--------------|-------------|----------|
| `telegram-notifications` | Scheduler Service | Notification Service | События для отправки Telegram уведомлений |
| `websocket-notifications` | Scheduler Service | Notification Service | События для WebSocket уведомлений |

### Последовательность обработки напоминания

1. **Scheduler Service** проверяет задачи в PostgreSQL
2. При наступлении времени напоминания создается событие `NotificationEvent`
3. Событие публикуется в Kafka топик `telegram-notifications` или `websocket-notifications`
4. **Notification Service** потребляет событие из Kafka
5. Уведомление отправляется через Telegram Bot API или WebSocket
6. Статус обновления сохраняется в PostgreSQL

### REST API взаимодействие

```
Frontend ──HTTP──▶ Nginx ──HTTP──▶ Todo Service
                                  │
                                  ▼
                            PostgreSQL
```

### WebSocket взаимодействие

```
Frontend ──WS──▶ Nginx ──WS──▶ Notification Service
                                │
                                ▼
                              Redis (Pub/Sub)
```

## 📡 API

### Основные эндпоинты Todo Service

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| POST | `/api/auth/register` | Регистрация пользователя |
| POST | `/api/auth/login` | Аутентификация |
| GET | `/api/tasks` | Получить список задач |
| POST | `/api/tasks` | Создать задачу |
| PUT | `/api/tasks/{id}` | Обновить задачу |
| DELETE | `/api/tasks/{id}` | Удалить задачу |
| GET | `/api/categories` | Получить категории |
| POST | `/api/categories` | Создать категорию |

### WebSocket эндпоинты

| Эндпоинт | Описание |
|----------|----------|
| `/ws/notifications` | Получение уведомлений в реальном времени |

## 📦 Релизы

### Версионирование

Проект использует [Semantic Versioning](https://semver.org/lang/ru/):
- **MAJOR** - несовместимые изменения API
- **MINOR** - новая функциональность
- **PATCH** - исправления ошибок

### Подготовка релиза

1. **Обновите версию в pom.xml**:
   ```bash
   # В каждом сервисе обновите версию
   mvn versions:set -DnewVersion=1.0.0
   ```

2. **Создайте Git тег**:
   ```bash
   git add .
   git commit -m "release: v1.0.0"
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

3. **Соберите Docker образы**:
   ```bash
   docker compose build
   ```

4. **Протестируйте сборку**:
   ```bash
   docker compose up -d
   docker compose ps
   ```

5. **Создайте Release на GitHub**:
   - Перейдите в раздел Releases
   - Нажмите "Create a new release"
   - Выберите тег
   - Добавьте описание изменений (CHANGELOG)
   - Опубликуйте релиз

### CI/CD рекомендации

Для автоматизации релизов рекомендуется настроить:
- GitHub Actions для сборки и тестирования
- Автоматическую публикацию Docker образов в Docker Hub
- Автоматическое создание релизов при пуше тега

## 📄 Лицензия

Этот проект распространяется под лицензией MIT License.

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🤝 Вклад в проект

1. Создайте форк репозитория
2. Создайте ветку для вашей фичи (`git checkout -b feature/amazing-feature`)
3. Закоммитьте изменения (`git commit -m 'Add some amazing feature'`)
4. Отпушьте в ветку (`git push origin feature/amazing-feature`)
5. Создайте Pull Request

## 📞 Контакты

- Email: ваш-email@example.com
- Telegram: @your-username

---

**Статус проекта**: В разработке ⚠️
