# DevOps Portfolio Platform

Cloud-native portfolio application for a DevOps internship project.

## Dev stack

- Backend: Spring Boot 3, Java 21, Spring Security, JPA, Actuator, Prometheus metrics
- Frontend: React + Vite
- Database: PostgreSQL

Flyway is not used in this first version. The backend uses JPA `ddl-auto=update` for local development.

## Structure

```text
apps/backend
apps/frontend
```

## Local development

Backend:

```bash
cd apps/backend
mvn spring-boot:run
```

Frontend:

```bash
cd apps/frontend
npm install
npm run dev
```

Default local admin:

```text
admin / admin12345
```

## Useful endpoints

```text
GET  /api/portfolio
POST /api/comments
POST /api/auth/login
GET  /api/admin/comments
GET  /actuator/health
GET  /actuator/prometheus
GET  /swagger-ui.html
```
