# Notes Management API

A RESTful backend service for creating, updating, retrieving, and deleting notes using Java, Spring Boot, and MongoDB.

## Tech Stack
- Java 17
- Spring Boot
- Spring Data MongoDB
- Bean Validation
- Springdoc OpenAPI (Swagger UI)
- Testcontainers (MongoDB integration tests)

## Run Locally
1. Start MongoDB on `mongodb://localhost:27017`.
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Run the app:
   ```bash
   mvn spring-boot:run
   ```

## Run Tests
```bash
mvn test
```

Integration tests use Testcontainers and will run when Docker is available.

## Run with Docker
```bash
docker compose up --build
```

## API Base URL
`http://localhost:8080/api/notes`

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints
- `POST /api/notes` - Create a note
- `GET /api/notes` - Get all notes (paginated)
- `GET /api/notes/{id}` - Get note by id
- `PUT /api/notes/{id}` - Update a note
- `DELETE /api/notes/{id}` - Delete a note

## Pagination and Sorting
`GET /api/notes` supports the following query parameters:
- `page` (default: `0`)
- `size` (default: `10`)
- `sortBy` (default: `createdAt`)
- `sortDir` (default: `desc`, allowed: `asc` or `desc`)

Example:
```http
GET /api/notes?page=0&size=5&sortBy=title&sortDir=asc
```

## Sample Create Request
```json
{
  "title": "Daily Plan",
  "content": "Finish Spring Boot API tasks"
}
```
