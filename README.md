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
- Postman for testing uploads

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
│   └── resources/               → application.properties
├── storage-common/              → Shared StorageService interface and metadata model
├── storage-file-system/         → File-based implementation of StorageService
├── storage-object-storage/      → Object-based implementation (MinIO)
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
- Curl or Postman (for API Testing)

### 1. Clone the repository

```text
git clone https://github.com/yourusername/package-manager.git
cd package-manager
```

### 2. Start Docker containers (PostgreSQL)

```bash
docker-compose up -d
```

### 3. Start MinIO (if using object-storage strategy)

```bash
docker run -d -p 9002:9000 -p 9003:9001 --name minio2 --restart always -e "MINIO_ROOT_USER=admin" -e "MINIO_ROOT_PASSWORD=12345678" quay.io/minio/minio server /data --console-address ":9003"
```

Then visit http://localhost:9001, log in with admin / 12345678 and create a new bucket (e.g. packages) called **packages** before using the installation API.

### 3. Build and run the application

```bash
# Clean and build the project
mvn clean install
```

```bash
# Go to the application folder
cd package-manager-app
```

```bash
# Run the application (from root directory)
mvn spring-boot:run 
```

Application will be accessible at: http://localhost:8080

---

## 🧰 Configuration

### 🔧 application.properties

```bash
spring.application.name=package-manager

# PostgreSQL bağlantısı
spring.datasource.url=jdbc:postgresql://localhost:5432/repsydb
spring.datasource.username=repsyuser
spring.datasource.password=repsy123

# Hibernate/JPA ayarları
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Sunucu portu (istersen özelleştirebilirsin)
server.port=8080

# Dosya sistemi kullanmak için:
#storage.strategy=file-system
storage.strategy=object-storage


# Bellek içi strateji kullanmak için:
# storage.strategy=in-memory
minio.url=http://localhost:9000
minio.accessKey=admin
minio.secretKey=12345678
minio.bucket=packages

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
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

## ⚠️ Note on External Dependencies

This project depends on private Maven artifacts hosted on [Repsy.io](https://repsy.io).

To build this project, make sure your `~/.m2/settings.xml` contains your Repsy credentials:

```xml
<server>
  <id>repsy</id>
  <username>your_repsy_username</username>
  <password>your_repsy_token</password>
</server>
```

---

## 🌐 Project Links

🔗 GitHub Repository: https://github.com/HuseyinSelen/REST-API-Implementation.git

🔗 Repsy Maven Repository: https://repsy.io/mvn/huseyin/rest-api-implementation

--- 

## 📄 License

MIT License © 2025 Hüseyin SELEN
