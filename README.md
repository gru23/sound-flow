# Sound Flow

Backend system for the **Sound Flow** application, developed as part of a diploma thesis. The system supports user registration, audio file uploads, and asynchronous separation of audio tracks into stems.

## Contents

- [System Overview](#system-overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Running with Docker](#running-with-docker)
- [Configuration](#configuration)
- [API](#api)
- [Local Development](#local-development)
- [Database and Storage](#database-and-storage)

## System Overview

Sound Flow is a service-based application for audio processing:

1. The backend receives a request and stores the input audio file.
2. The separation request is stored as a job and sent to the worker service through RabbitMQ.
3. The worker runs Demucs processing in a Docker container.
4. The generated stems are archived into a ZIP file.
5. The client tracks the job status and downloads the completed result.

## Architecture

```text
Client
   |
   v
Backend (Spring Boot :8080) ---- PostgreSQL
   |
   +---------------------------- RabbitMQ (separationQueue)
                                      |
                                      v
                          Separation Worker
                                      |
                                      v
                         Demucs Docker image

Backend and worker share a Docker volume: /storage
```

The repository contains two Spring Boot services:

- `backend-sound-flow` - REST API, authentication, user management, audio uploads, and separation job management.
- `separation-worker` - RabbitMQ consumer that performs separation and updates job statuses.

## Technologies

- Java 17
- Spring Boot 4.x
- Spring Web, Spring Data JPA, and Spring Security
- JWT authentication
- PostgreSQL 18
- RabbitMQ 4
- Docker and Docker Compose
- Demucs through the `voxextractlabs/vox-demucs:1.0.0` image
- Maven Wrapper

## Running with Docker

### Prerequisites

- Docker Desktop with Linux containers enabled
- Docker Compose
- Permission to use the Docker socket for `separation-worker`, since the worker starts the Demucs Docker container

### 1. Configure secrets

Do not commit real passwords, OAuth secrets, JWT keys, or email credentials. Create the `.env` files locally and keep them out of Git history.

The root `.env` file is used by Docker Compose for the PostgreSQL password:

```env
POSTGRESQL_ROOT_PASSWORD=replace-this-value
```

Set at least the following values in both `backend-sound-flow/.env` and `separation-worker/.env`:

```env
POSTGRESQL_SERVER_URL=jdbc:postgresql://postgres:5432/sound_flow
POSTGRESQL_ROOT_USERNAME=postgres
POSTGRESQL_ROOT_PASSWORD=replace-this-value
```

The backend additionally expects:

```env
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
JWT_TOKEN_SECRET=your-long-random-secret
EMAIL_APP_PASSWORD=your-mail-app-password
```

The `POSTGRESQL_ROOT_PASSWORD` value must be identical in the root `.env` file and in both service-specific env files.

### 2. Start the services

From the repository root, run:

```bash
docker compose up --build
```

The services will be available at:

| Service | Address |
| --- | --- |
| Backend API | `http://localhost:8080` |
| PostgreSQL | `localhost:5433` |
| RabbitMQ AMQP | `localhost:5672` |
| RabbitMQ Management UI | `http://localhost:15672` |

The default RabbitMQ username and password are both `guest`. Data is stored in the `postgres_data`, `rabbitmq_data`, and `separation_storage` Docker volumes.

To stop the services:

```bash
docker compose down
```

Use `docker compose down -v` only when you intentionally want to remove persistent data.

## Configuration

The main configuration values are located in the `application.properties` files of both services.

- The backend listens on port `8080`.
- The maximum upload file size is `20MB`.
- PostgreSQL and RabbitMQ are addressed as `postgres` and `rabbitmq` on the Docker network.
- Shared storage is mounted at `/storage`.
- The RabbitMQ queue for separation jobs is named `separationQueue`.
- Supported separation options are `FOUR_STEMS` and `VOCALS`.

## API

The backend uses JWT authentication. Send the following header with protected requests:

```http
Authorization: Bearer <access-token>
```

### Authentication

| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/auth/registration` | Register a local user |
| `POST` | `/auth/login` | Log in and issue tokens |
| `POST` | `/auth/logout` | Log out |
| `GET` | `/auth/check` | Check the current session |
| `POST` | `/auth/refresh` | Refresh the access token |
| `GET` | `/auth/verify?token=...` | Verify an account by email |
| `POST` | `/auth/reset` | Request a password reset |
| `POST` | `/auth/reset-confirm` | Confirm a new password |
| `POST` | `/oauth/google/login` | Log in with a Google ID token |

### Audio and Separation

| Method | Route | Description |
| --- | --- | --- |
| `POST` | `/audio/upload` | Upload an audio file using the `file` multipart field |
| `POST` | `/separations/separate` | Create a separation job; multipart fields are `clientId`, `file`, and `option` |
| `GET` | `/separations/{id}` | Get a separation job |
| `GET` | `/separations/status/{jobId}` | Get the processing status |
| `GET` | `/separations/download/{jobId}` | Download the ZIP archive containing the stems |
| `DELETE` | `/separations/{id}` | Delete a job |
| `GET` | `/clients/{clientId}/separations` | List a user's jobs |

Example separation request:

```bash
curl -X POST http://localhost:8080/separations/separate \
  -H "Authorization: Bearer <access-token>" \
  -F "clientId=1" \
  -F "file=@song.mp3" \
  -F "option=FOUR_STEMS"
```

## Local Development

To run the services with Maven, provide accessible PostgreSQL and RabbitMQ instances and configure the appropriate local values in the `.env` files.

Backend:

```bash
cd backend-sound-flow
./mvnw spring-boot:run
```

Worker:

```bash
cd separation-worker
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

Tests and build:

```bash
./mvnw test
./mvnw clean package
```

## Database and Storage

The `db/` directory contains an SQL export of the previously used local database. The export includes data and paths from the former Windows environment, so it should not be used as a Docker database initializer without reviewing and adapting those paths.

In Docker mode, the backend and worker use the `sound_flow` PostgreSQL database. Audio files and separation results are shared through the `separation_storage` volume, mounted as `/storage` in both containers.

## Project Status

This project is the backend component of the **Sound Flow** diploma thesis. The frontend client and additional project documentation may be maintained in separate repositories or added as separate modules.
