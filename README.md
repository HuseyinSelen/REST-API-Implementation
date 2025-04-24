# 📦 Package Manager API

Package Manager is a Spring Boot-based REST API for uploading and downloading software packages along with their metadata and dependencies.

---

## 🚀 Features

- Upload packages with associated `meta.json` and `package.rep` files
- List and download uploaded packages
- Supports dynamic storage strategies: `file-system` or `object-storage` (e.g., MinIO)
- PostgreSQL for metadata persistence
- JSON metadata structure with dependency resolution
- Clean and modular Maven multi-module structure
- Configurable via `application.properties` or environment variables

---

## 🛠️ Tech Stack

- Java 17 (LTS)
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Docker & Docker Compose
- PostgreSQL
- Maven (multi-module)
- MinIO-compatible object storage (optional)
- Curl for testing uploads

---

## 📁 Project Structure

```text
package-manager/
├── package-manager-app/         → Main Spring Boot application
│   ├── controller/              → REST API endpoints
│   ├── entity/                  → JPA entities (Package, Dependency)
│   ├── model/                   → DTOs and metadata models
│   ├── repository/              → Spring Data JPA repositories
│   ├── config/                  → Storage strategy configuration
│   ├── resources/               → application.properties
├── storage-file-system/         → File-based implementation of StorageService
```

---

## 📦 JSON Format (meta.json)

```json
{
  "name": "testpackage",
  "version": "1.0.0",
  "author": "John Doe",
  "dependencies": [
    {
      "package": "even",
      "version": "3.4.7"
      }, {
      "package": "math",
      "version": "4.2.8"
      }, {
      "package": "std",
      "version": "1.2.0"
      }, {
      "package": "repsy",
      "version": "1.0.3"
      }, {
      "package": "client",
      "version": "3.3.0"
      }
      
  ]
}
```

---

## 🛠️ Installation

### Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose

### 1. Clone the repository

```text
git clone https://github.com/yourusername/package-manager.git
cd package-manager
```

### 2. Start Docker containers (PostgreSQL)

```bash
docker-compose up -d
```

### 3. Build and run the application

```bash
# Clean and build the project
mvn clean install

# Run the application (from root directory)
mvn spring-boot:run -pl package-manager-app
```

Application will be accessible at: http://localhost:8080

---

## 🧰 Configuration

### 🔧 application.properties

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/repsydb
spring.datasource.username=repsyuser
spring.datasource.password=repsy123

storage.strategy=file-system  # or object-storage
```

---

## 🧪 REST API Endpoints

### 📤 Upload Package (Postman)

- **Method**: `POST`
- **URL**: `http://localhost:8080/testpackage/1.0.0`
- **Form Data**:
  - `meta`: `meta.json` file
  - `file`: `test.zip` (any binary file)

### 📥 Download Files

- **Meta File**:  
  `GET http://localhost:8080/api/packages/testpackage/1.0.0/meta.json`

- **Package File**:  
  `GET http://localhost:8080/api/packages/testpackage/1.0.0/package.rep`

### 📄 Get Package Info as JSON

- **URL**:  
  `GET http://localhost:8080/api/packages/testpackage/1.0.0`

---

## 🧪 Sample Upload with Curl (Git Bash)

```bash
curl -X POST http://localhost:8080/testpackage/1.0.0 \
  -F "meta=@meta.json" \
  -F "file=@test.zip;filename=package.rep"
```

---

## 👨‍💻 Author

Hüseyin Selen
Full Stack Developer Candidate

---

## 📄 License

This project is licensed under the MIT License.
