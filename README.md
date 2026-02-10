# StoryHub Backend 🖋️

**StoryHub Backend** is a robust and scalable platform for blog management, built with a focus on clean code, maintainability, and high test coverage. The project has undergone a significant migration from a monolithic structure to a **Hexagonal Architecture** (Ports and Adapters), ensuring that business logic remains isolated from external infrastructure.

---

## 🏗️ Architecture: Hexagonal (Ports & Adapters)

The project follows a strict separation of concerns to maximize modularity and testability:

- **Domain Layer**: Contains pure business logic, entities, and repository interfaces (Ports).
- **Application Layer**: Orchestrates use cases, containing services and DTO mappers.
- **Infrastructure Layer**: Handles external details like entry points (REST Controllers), Security (JWT), Persistence (Spring Data JPA), and Utilities.

---

## 🚀 Technologies & Features

### Tech Stack
- **Languages/Frameworks**: Java 17, Spring Boot 3.2.x.
- **Security**: Spring Security + JWT (Stateless).
- **Persistence**: PostgreSQL (Production) / H2 (Testing).
- **Mappers**: MapStruct for high-performance DTO/Entity conversion.
- **Rate Limiting**: Bucket4j for protecting critical endpoints (Login/Register).
- **Documentation**: SpringDoc OpenAPI (Swagger).
- **Testing**: JUnit 5, Mockito, AssertJ.
- **Coverage**: JaCoCo (Logic coverage milestone: **100%**).

### Key Features
- **Blog Management**: Full CRUD with partial updates and unique view tracking.
- **Interactive Features**: Like/Unlike system with state synchronization (`isLiked`).
- **Comments**: Hierarchical (nested) comments with logical deletion and ascending order.
- **User Profiles**: Public profiles with paginated blogs and private profile editing.
- **Resilience**: Integrated Rate Limiting to prevent brute-force attacks on auth endpoints.

---

## 🧪 Testing & Quality

We maintain a high standard of quality with a focus on comprehensive testing:
- **100% Logic Coverage**: All core services (`BlogService`, `UserService`, `CommentService`) are fully covered by unit and integration tests.
- **Maintainable Tests**: Clean code practices applied to the test suite, including single-invocation lambdas in `assertThrows` and meaningful assertions.

### Run Tests
```bash
mvn test
```

---

## 🛠️ Getting Started

### Prerequisites
- JDK 17
- Maven 3.x
- PostgreSQL

### Local Development
1. Clone the repository.
2. Configure your database credentials in `src/main/resources/application.properties`.
3. Run the application:
```bash
mvn spring-boot:run
```

---

## 📜 Standard Commits
This project follows the **Conventional Commits** specification to maintain a clear and readable history:
- `feat`: New features.
- `fix`: Bug fixes.
- `refactor`: Structural changes (like the Hexagonal migration).
- `test`: Coverage and test suite improvements.
- `docs`: Documentation updates.
