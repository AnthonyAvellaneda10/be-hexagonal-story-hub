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
- **Storage**: AWS S3 for secure and scalable image management.
- **Persistence**: PostgreSQL (Production) / H2 (Testing).
- **Mappers**: MapStruct for high-performance DTO/Entity conversion.
- **Rate Limiting**: Bucket4j for protecting critical endpoints (Login/Register).
- **Documentation**: SpringDoc OpenAPI (Swagger).
- **Testing**: JUnit 5, Mockito, AssertJ.
- **Coverage**: JaCoCo (Logic coverage milestone: **100%**).

### Key Features
- **S3 Image Storage**: Robust integration with AWS S3 for managing profile pictures, blog banners, and cover images.
- **Secure Image Delivery**: Automated generation of temporary **Pre-signed URLs** (10-minute validity) for all image responses.
- **Image Validation**: Centralized validation system for file size (5MB limit) and allowed formats (`.jpg`, `.png`, `.webp`, `.svg`).
- **JSON Compatibility**: Native support for both `camelCase` and `snake_case` naming conventions in Blog DTOs to simplify frontend integration.
- **Blog Management**: Full CRUD with optimized partial updates and dedicated endpoints for image management.
- **Interactive Features**: Like/Unlike system with state synchronization (`isLiked`).
- **Comments**: Hierarchical (nested) comments with logical deletion and ascending order.
- **User Profiles**: Public profiles with paginated blogs and private profile image updates via S3.
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

## ⚒️ Getting Started

### Prerequisites
- Docker & Docker Compose (Recommended)
- **OR** JDK 17, Maven 3.x, and PostgreSQL

### 🐳 Deployment with Docker (Easiest)
1. Clone the repository.
2. Create your `.env` file in the root directory (use `.env.example` as a template).
3. Build and keep the containers running:
```bash
docker-compose up -d --build
```
4. Access the API documentation at: `http://localhost:8080/swagger-ui.html`

### 💻 Local Development (Manual)
1. Clone the repository.
2. Configure your database and app credentials in `src/main/resources/application.yml` or via environment variables.
3. Run the application:
```bash
mvn spring-boot:run
```

---

## 📖 API Documentation
The project includes interactive documentation powered by **SpringDoc OpenAPI**. Once the application is running, you can explore and test the endpoints at:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📜 Standard Commits
This project follows the **Conventional Commits** specification to maintain a clear and readable history:
- `feat`: New features.
- `fix`: Bug fixes.
- `refactor`: Structural changes.
- `test`: Coverage and test suite improvements.
- `docs`: Documentation updates.
- `chore`: Configuration and maintenance.
