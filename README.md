# Package Manager Application

This is a multi-module Maven project that allows package upload and download via REST API using Spring Boot and PostgreSQL.

## Modules

- **package-manager-app**: Spring Boot application with REST controllers and database integration
- **storage-file-system**: Strategy implementation to save files to the file system

## Features

- Upload a package: `POST /{name}/{version}`
- Download metadata: `GET /{name}/{version}/meta.json`
- Download binary: `GET /{name}/{version}/package.rep`

## How to Run

1. Start Docker: `docker-compose up -d`
2. Run the application:

```bash
cd package-manager
mvn spring-boot:run -pl package-manager-app