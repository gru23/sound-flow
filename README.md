# Sound Flow

Backend sistem aplikacije **Sound Flow**, razvijen u okviru diplomskog rada. Sistem omogućava registraciju korisnika, upload audio fajlova i asinhronu separaciju audio zapisa na stemove.

## Sadržaj

- [Pregled sistema](#pregled-sistema)
- [Arhitektura](#arhitektura)
- [Tehnologije](#tehnologije)
- [Pokretanje pomoću Dockera](#pokretanje-pomoću-dockera)
- [Konfiguracija](#konfiguracija)
- [API](#api)
- [Lokalni razvoj](#lokalni-razvoj)
- [Baza podataka i storage](#baza-podataka-i-storage)

## Pregled sistema

Sound Flow je servisna aplikacija za obradu audio fajlova:

1. Backend prima zahtjev i čuva ulazni audio fajl.
2. Zahtjev za separaciju se upisuje kao job i šalje worker servisu kroz RabbitMQ.
3. Worker pokreće Demucs obradu u Docker kontejneru.
4. Generisani stemovi se arhiviraju u ZIP fajl.
5. Klijent prati status job-a i preuzima završeni rezultat.

## Arhitektura

```text
Klijent
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

Backend i worker dijele Docker volume: /storage
```

Repozitorijum sadrži dva Spring Boot servisa:

- `backend-sound-flow` - REST API, autentifikacija, korisnici, audio upload i upravljanje separation job-ovima.
- `separation-worker` - RabbitMQ consumer koji izvršava separaciju i ažurira status job-a.

## Tehnologije

- Java 17
- Spring Boot 4.x
- Spring Web, Spring Data JPA i Spring Security
- JWT autentifikacija
- PostgreSQL 18
- RabbitMQ 4
- Docker i Docker Compose
- Demucs preko image-a `voxextractlabs/vox-demucs:1.0.0`
- Maven Wrapper

## Pokretanje pomoću Dockera

### Preduslovi

- Docker Desktop sa uključenim Linux containers režimom
- Docker Compose
- Omogućena upotreba Docker socketa za `separation-worker`, jer worker pokreće Demucs Docker kontejner

### 1. Konfiguracija tajni

Ne commitujte stvarne lozinke, OAuth tajne, JWT ključeve ili mail kredencijale. Napravite `.env` fajlove izvan Git istorije.

Root `.env` koristi Docker Compose za PostgreSQL lozinku:

```env
POSTGRESQL_ROOT_PASSWORD=promijeni-ovu-vrijednost
```

U `backend-sound-flow/.env` i `separation-worker/.env` postavite najmanje:

```env
POSTGRESQL_SERVER_URL=jdbc:postgresql://postgres:5432/sound_flow
POSTGRESQL_ROOT_USERNAME=postgres
POSTGRESQL_ROOT_PASSWORD=promijeni-ovu-vrijednost
```

Backend dodatno očekuje:

```env
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
JWT_TOKEN_SECRET=your-long-random-secret
EMAIL_APP_PASSWORD=your-mail-app-password
```

Vrijednost `POSTGRESQL_ROOT_PASSWORD` mora biti ista u root `.env` fajlu i u env fajlovima oba servisa.

### 2. Pokretanje servisa

Iz root direktorijuma repozitorijuma pokrenite:

```bash
docker compose up --build
```

Servisi će biti dostupni na:

| Servis | Adresa |
| --- | --- |
| Backend API | `http://localhost:8080` |
| PostgreSQL | `localhost:5433` |
| RabbitMQ AMQP | `localhost:5672` |
| RabbitMQ Management UI | `http://localhost:15672` |

Podrazumijevani RabbitMQ korisnik je `guest`, a lozinka `guest`. Podaci se čuvaju u Docker volume-ima `postgres_data`, `rabbitmq_data` i `separation_storage`.

Za zaustavljanje servisa:

```bash
docker compose down
```

Za zaustavljanje uz brisanje persistent podataka koristite `docker compose down -v` samo kada je to namjerno.

## Konfiguracija

Glavne vrijednosti se nalaze u `application.properties` fajlovima oba servisa.

- Backend sluša na portu `8080`.
- Maksimalna veličina upload fajla je `20MB`.
- PostgreSQL i RabbitMQ se u Docker mreži adresiraju kao `postgres` i `rabbitmq`.
- Zajednički storage je montiran na `/storage`.
- RabbitMQ queue za poslove separacije zove se `separationQueue`.
- Podržane opcije separacije su `FOUR_STEMS` i `VOCALS`.

## API

Backend koristi JWT. Za zaštićene rute šalje se zaglavlje:

```http
Authorization: Bearer <access-token>
```

### Autentifikacija

| Metoda | Ruta | Opis |
| --- | --- | --- |
| `POST` | `/auth/registration` | Registracija lokalnog korisnika |
| `POST` | `/auth/login` | Prijava i izdavanje tokena |
| `POST` | `/auth/logout` | Odjava |
| `GET` | `/auth/check` | Provjera trenutne sesije |
| `POST` | `/auth/refresh` | Obnavljanje access tokena |
| `GET` | `/auth/verify?token=...` | Verifikacija naloga putem e-maila |
| `POST` | `/auth/reset` | Zahtjev za reset lozinke |
| `POST` | `/auth/reset-confirm` | Potvrda nove lozinke |
| `POST` | `/oauth/google/login` | Prijava pomoću Google ID tokena |

### Audio i separacija

| Metoda | Ruta | Opis |
| --- | --- | --- |
| `POST` | `/audio/upload` | Upload audio fajla, multipart polje `file` |
| `POST` | `/separations/separate` | Kreiranje separation job-a; multipart polja su `clientId`, `file` i `option` |
| `GET` | `/separations/{id}` | Dohvatanje separation job-a |
| `GET` | `/separations/status/{jobId}` | Dohvatanje statusa obrade |
| `GET` | `/separations/download/{jobId}` | Preuzimanje ZIP arhive stemova |
| `DELETE` | `/separations/{id}` | Brisanje job-a |
| `GET` | `/clients/{clientId}/separations` | Lista job-ova korisnika |

Primjer kreiranja zahtjeva za separaciju:

```bash
curl -X POST http://localhost:8080/separations/separate \
  -H "Authorization: Bearer <access-token>" \
  -F "clientId=1" \
  -F "file=@song.mp3" \
  -F "option=FOUR_STEMS"
```

## Lokalni razvoj

Za pokretanje servisa iz Maven-a potrebno je obezbijediti dostupne PostgreSQL i RabbitMQ instance, kao i odgovarajuće lokalne vrijednosti u `.env` fajlovima.

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

Na Windowsu koristite `mvnw.cmd` umjesto `./mvnw`.

Testovi i build:

```bash
./mvnw test
./mvnw clean package
```

## Baza podataka i storage

Direktorijum `db/` sadrži SQL export ranije lokalne baze. Export uključuje i podatke i putanje vezane za prethodno Windows okruženje, pa ga ne treba bez provjere koristiti kao Docker inicijalizaciju baze.

U Docker režimu backend i worker koriste PostgreSQL bazu `sound_flow`, a audio fajlovi i rezultati separacije dijele se kroz volume `separation_storage`, montiran kao `/storage` u oba kontejnera.

## Status projekta

Projekat je backend dio diplomskog rada **Sound Flow**. Frontend klijent i dodatna projektna dokumentacija mogu se održavati u zasebnim repozitorijumima ili dodati kao posebni moduli.