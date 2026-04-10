# StayNova — Hotel Booking Platform

> Платформа для бронирования отелей на микросервисной архитектуре. Позволяет пользователям искать отели и номера, оформлять бронирования и получать уведомления на email. Основана на микросервисной архитектуре.

---

## Содержание

- [Технологии](#технологии)
- [Сервисы](#сервисы)
- [Начало работы](#начало-работы)
- [Архитектура бекенда](#архитектура-бекенда)
- [Команда проекта](#команда-проекта)

---

## Технологии

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" />
  <img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" />
  <img src="https://img.shields.io/badge/MinIO-C72E49?style=for-the-badge&logo=minio&logoColor=white" />
  <img src="https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D" />
  <img src="https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white" />
  <img src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white" />
  <img src="https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=kibana&logoColor=white" />
  <img src="https://img.shields.io/badge/Liquibase-2962FF?style=for-the-badge&logo=liquibase&logoColor=white" />
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" />
</p>

---

## Сервисы

| Сервис                   | Порт | Назначение                      |
| ------------------------ | ---- | ------------------------------- |
| **api-gateway**          | 8089 | Единая точка входа.             |
| **auth-service**         | —    | Аутентификация и авторизация.   |
| **user-service**         | —    | Управление пользователями.      |
| **booking-service**      | —    | Отели, номера, бронирования.    |
| **payment-service**      | 8085 | Обработка платежей.             |
| **notification-service** | —    | Уведомления через Kafka         |
| **service-observer**     | 8084 | Eureka Server — реестр сервисов |

---

## Начало работы

### Требования

- Docker Desktop

### Установка и запуск

```bash
git clone https://github.com/Andrey999r/hotel-microservices.git

cd hotel-microservices

docker compose up -d
```

### Доступные адреса после запуска

| Сервис           | URL                                   |
| ---------------- | ------------------------------------- |
| Frontend         | http://localhost:5173                 |
| Swagger UI       | http://localhost:8089/swagger-ui.html |
| Eureka Dashboard | http://localhost:8084                 |
| Grafana          | http://localhost:3000                 |
| Kibana           | http://localhost:5601                 |
| Prometheus       | http://localhost:9090                 |
| MinIO Console    | http://localhost:9001                 |

## Архитектура бекенда

В данный момент большинство сервисов сейчас на классическом layered-подходе. **user-service** же построен на гексагональной архитектуре и разбит на три Maven-модуля, поэтому в этом разделе будет информация только о нем, как о самом продвинутом, с точки зрения технологий и архитектуры, сервисе. Остальной фунционал приложения будет переведен на схожую архитектуру по мере времени.

`user-service-domain` — чистое ядро без зависимостей на Spring, JPA или Kafka. Содержит модели `User` и `Role`, value objects с валидацией в конструкторе (`Login`, `Email`, `Password`, `PicturePath`) и порты — интерфейсы для всего внешнего (`UserRepository`, `NotificationOutputPort`, `ObjectStorageOutputPort`, `AdminSecretOutputPort` и другие).

`user-service-application` — юзкейсы. Каждый сценарий — отдельный класс на один порт: `RegisterUserService`, `UpdateProfileService`, `UploadPhotoService` и так далее. Тестируются изолированно через Mockito — без Spring-контекста и без базы данных.

`user-service-infrastructure` — всё внешнее: REST-контроллеры, JPA-адаптеры, Kafka-адаптер с Redis-дедупликацией, MinIO-адаптер. Инфраструктура реализует порты домена и не знает о бизнес-логике ничего сверх контракта.

Главное преимущество — замена любого внешнего компонента без правки домена. Сменить PostgreSQL на другую БД или Kafka на RabbitMQ — значит написать новый адаптер, не трогая юзкейсы.

## Команда проекта

- [Andrey999r](https://github.com/Andrey999r) — Backend Engineer
