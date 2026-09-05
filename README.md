# Locking Demo

A Spring Boot demo project showing the difference between **optimistic** and
**pessimistic** locking when handling concurrent writes, using a simple
movie-seat-booking scenario as the example.

## Scenario

Two threads race to book the same seat at the same time. Depending on the
endpoint you call, the seat is booked using either:

- **Optimistic locking** — via JPA `@Version` on `Seat`. Both threads read the
  seat, but only the first `save()` succeeds; the second fails with an
  `OptimisticLockingFailureException` because the version it read is stale.
- **Pessimistic locking** — via a DB-level row lock (`findByIdAndLock`). The
  first thread to reach the query locks the row; the second thread blocks
  until the lock is released, then sees the seat is already booked and fails
  with a `RuntimeException`.

## Tech stack

- Java 21
- Spring Boot 3.4.0 (Web, Data JPA)
- PostgreSQL
- springdoc-openapi (Swagger UI)
- Lombok

## Project structure

```
src/main/java/com/example/lockingdemo
├── controller/BookingTestController.java        # REST endpoints to trigger each test
├── entity/Seat.java                              # Seat entity (id, movieName, booked, @Version)
├── repository/SeatRepository.java                # findByIdAndLock (pessimistic lock query)
└── service/
    ├── MovieTicketBookingService.java            # bookSeat / bookSeatWithPessimistic
    ├── OptimisticSeatBookingTestService.java      # spins up 2 threads for optimistic test
    └── PessimisticSeatBookingTestService.java     # spins up 2 threads for pessimistic test
```

## Prerequisites

- JDK 21
- PostgreSQL running locally with a database named `locking-demo`
- A `seats` table (created automatically via `spring.jpa.hibernate.ddl-auto=update`)
  with at least one row to test against

Update credentials in `src/main/resources/application.properties` if they
differ from the defaults:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/locking-demo
spring.datasource.username=postgres
spring.datasource.password=admin
```

## Running the app

```bash
./mvnw spring-boot:run
```

The app starts on **port 9191**.

## Testing the locking behavior

Insert a seat row first, e.g.:

```sql
INSERT INTO seats (movie_name, booked, version) VALUES ('Inception', false, 0);
```

Then hit either endpoint with that seat's id and watch the application logs:

```
GET http://localhost:9191/booking/optimistic/{seatId}
GET http://localhost:9191/booking/pessimistic/{seatId}
```

Each call starts two threads that both attempt to book the same seat
concurrently — one thread succeeds, the other fails, and the logs show why.

## API docs

Swagger UI is available at:

```
http://localhost:9191/swagger-ui.html
```
