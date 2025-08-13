# Sample Spring - Hexagonal Architecture with Virtual Threads

## Project Overview

This project demonstrates the implementation of hexagonal architecture (Ports and Adapters) using Spring Boot 3.5.0 and Java 21 with Virtual Threads for high-throughput I/O operations without using reactive programming.

## Technology Stack

- **Java 21** - With Virtual Threads support
- **Spring Boot 3.5.0** - Core framework
- **Spring Web** - REST API implementation
- **Spring Validation** - Input validation
- **Maven** - Build tool
- **JUnit 5** - Test framework
- **Testcontainers** - Integration testing
- **WireMock** - API mocking

## Architectural Overview

The project follows hexagonal architecture (also known as Ports and Adapters) to separate business logic from technical concerns:

```
src/main/java/guichafy/sample_api/
├── application/
│   ├── ports/
│   │   ├── input/          # Use case interfaces
│   │   └── output/         # External adapter interfaces
│   └── usecases/           # Business logic implementation
├── domain/
│   ├── entities/           # Domain entities
│   └── valueobjects/       # Immutable value objects
└── infrastructure/
    ├── adapters/
    │   ├── input/web/      # REST controllers
    │   └── output/         # External API adapters
    └── config/             # Spring configuration
```

### Key Components

1. **Domain Layer**:
   - Core business logic
   - Entities: `Route`, `User`, `Todo`
   - Value Objects: `RouteId`, `UserId`, `TodoId`, `Email`
   - No external dependencies

2. **Application Layer**:
   - Use Cases: `RouteService`, `UserService`, `TodoService`
   - Input Ports: `GetRouteUseCase`, `GetRoutesUseCase`, etc.
   - Output Ports: `RouteApiPort`, `UserApiPort`, `TodoApiPort`
   - Business orchestration

3. **Infrastructure Layer**:
   - Controllers: `SitemapController`, `UserController`, `TodoController`
   - External Adapters: `ExternalRouteApiAdapter`, `UserApiAdapter`, `TodoAdapter`
   - Configuration: `ApplicationConfig`

## Virtual Threads Implementation

The project leverages Java 21 Virtual Threads for high-throughput I/O operations:

1. **Configuration** in `ApplicationConfig.java`:
   ```java
   @Bean("virtualThreadTaskExecutor")
   public AsyncTaskExecutor virtualThreadTaskExecutor() {
       return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
   }
   ```

2. **Usage in Services** (e.g., `RouteService.java`):
   ```java
   // Parallel execution using CompletableFuture with Virtual Threads
   CompletableFuture<Optional<Route>> routeFuture = routeApiPort.findRouteByIdAsync(routeId);
   CompletableFuture<Boolean> activeFuture = routeApiPort.isRouteActiveAsync(routeId);
   
   // Wait for both operations
   Optional<Route> routeOpt = routeFuture.get();
   Boolean isActive = activeFuture.get();
   ```

3. **Spring Boot Configuration** in `application.yml`:
   ```yaml
   spring:
     threads:
       virtual:
         enabled: true
   ```

## API Endpoints

The application provides several REST endpoints:

### Route API
- `GET /api/sitemap/routes/{id}` - Get route by ID
- `GET /api/sitemap/routes` - List all routes with filters
- `GET /api/sitemap/routes/active` - Get active routes
- `GET /api/sitemap/routes/method/{method}` - Get routes by HTTP method
- `GET /api/sitemap/routes/tags` - Get routes by tags

### User API
- User management endpoints implemented in `UserController`

### Todo API
- Todo management endpoints implemented in `TodoController`

## Building and Running

### Prerequisites
- Java 21 or higher
- Maven 3.8+

### Build Commands
```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

### Configuration
The application can be configured via `application.yml`:

```yaml
app:
  external-api:
    base-url: ${EXTERNAL_API_URL:https://api.example.com}
    timeout:
      connect: 10s
      read: 30s

spring:
  threads:
    virtual:
      enabled: true
```

## Testing

The project includes both unit and integration tests:

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IntegrationTest"
```

Integration tests use Testcontainers and WireMock for simulating external dependencies.

## Key Benefits

1. **Clean Architecture**:
   - Clear separation of concerns
   - Domain-driven design
   - Improved testability

2. **Virtual Threads**:
   - Familiar synchronous coding style
   - High performance for I/O operations
   - Thousands of concurrent operations with minimal overhead
   - Simpler than reactive programming

3. **Maintainability**:
   - Well-organized codebase
   - Easy to test isolated components
   - Clear dependency flow

## Running Development Tools

```bash
# Start in development mode with hot reload
mvn spring-boot:run
```

## Planned Future Enhancements

- Redis cache implementation
- Micrometer metrics
- Circuit breaker implementation
- OpenAPI documentation
- JWT authentication