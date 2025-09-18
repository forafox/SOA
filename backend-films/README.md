# Movies API

REST API for managing movies with PostgreSQL database.

## Project Structure

```
src/main/java/com/blps/
├── config/                 # Configuration classes
│   ├── Database.java      # Database connection management
│   ├── ObjectMapperProvider.java  # JSON serialization configuration
│   └── PATCH.java         # Custom PATCH HTTP method annotation
├── controller/            # REST controllers
│   ├── MovieController.java    # Movie REST endpoints
│   └── HelloController.java    # Simple hello endpoint
├── model/                 # Data models
│   ├── Movie.java         # Movie entity with validation
│   ├── Person.java        # Person record
│   ├── Coordinates.java   # Coordinates record
│   └── MovieGenre.java    # Movie genre enum
├── repository/            # Data access layer
│   ├── MovieRepository.java           # Repository interface
│   └── impl/
│       └── MovieRepositoryImpl.java   # JDBC implementation
├── service/               # Business logic layer
│   └── MovieService.java  # Movie business logic
├── Main.java             # Application entry point
└── RestApplication.java  # JAX-RS application configuration
```

## Features

- **Java 21** with modern language features
- **Records** for immutable data structures (Person, Coordinates)
- **Validation** using Jakarta Bean Validation annotations
- **Clean Architecture** with separation of concerns:
  - Controllers handle HTTP requests/responses
  - Services contain business logic
  - Repositories handle data access
  - Models represent domain entities
- **RESTful API** with standard HTTP methods
- **PostgreSQL** database integration

## API Endpoints

### Movies

- `POST /api/movies` - Create a new movie
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies` - Get movies with filtering, sorting, and pagination
- `PATCH /api/movies/{id}` - Update movie partially
- `DELETE /api/movies/{id}` - Delete movie by ID
- `DELETE /api/movies/oscarsCount/{count}` - Delete movies by oscars count
- `GET /api/movies/count/oscars-less-than/{count}` - Count movies with oscars less than specified
- `GET /api/movies/name-starts-with/{prefix}` - Get movies by name prefix

### Query Parameters for GET /api/movies

- `name` - Filter by movie name (contains)
- `genre` - Filter by movie genre
- `sort` - Sort field with optional `:desc` suffix (e.g., `name:desc`)
- `page` - Page number (default: 1)
- `size` - Page size (default: 20)

## Database Schema

The application expects the following PostgreSQL tables:

```sql
CREATE TABLE coordinates (
    id BIGSERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y DOUBLE PRECISION NOT NULL
);

CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    birthday DATE,
    height DOUBLE PRECISION NOT NULL CHECK (height > 0),
    weight BIGINT NOT NULL CHECK (weight > 0),
    passport_id VARCHAR NOT NULL
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    oscars_count BIGINT CHECK (oscars_count > 0),
    golden_palm_count BIGINT CHECK (golden_palm_count > 0),
    budget REAL CHECK (budget > 0),
    genre VARCHAR NOT NULL,
    coordinates_id BIGINT REFERENCES coordinates(id),
    screenwriter_id BIGINT REFERENCES person(id)
);
```

## Running the Application

1. Start PostgreSQL database
2. Create the database and tables as shown above
3. Update database connection settings in `Database.java` if needed
4. Run: `mvn clean compile exec:java -Dexec.mainClass="com.blps.Main"`

The API will be available at `http://localhost:8080/api/`

## Validation

The application includes comprehensive validation:

- **Movie**: name (not blank), coordinates (not null), genre (not null)
- **Person**: name (not blank), height (positive), weight (positive), passportID (not blank)
- **Coordinates**: x and y (not null)

Validation errors will result in `IllegalArgumentException` with descriptive messages.
