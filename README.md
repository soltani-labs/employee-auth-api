<div align="center">

# spring-jwt-employee-manager

**Spring Boot REST API avec authentification JWT et contrôle des rôles (USER / ADMIN)**

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen?style=flat-square\&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square\&logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=flat-square\&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square\&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square\&logo=apachemaven)

</div>

---

Les employees sont aussi les users du system.

Les admins peuvent créer, modifier, supprimer et lister les employees, et changer un user en admin, pendant que les users normal peuvent seulement faire login.

Construit avec **Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, et JJWT.**

---

## Les Features

* [x] Authentification basé sur JWT (stateless, pas de session sur le server)
* [x] Access control basé sur les roles: USER et ADMIN.
* [x] Chaque compte employee est aussi un compte login (username, password, roles) et un dossier de travail (full name, position, department, salary) dans la même entité.
* [x] Seulement les ADMIN peuvent:

  * [x] Créer un nouveau employee (ça crée aussi son login)
  * [x] Modifier ou supprimer un employee
  * [x] Lister tous les employees
  * [x] Promouvoir un USER en ADMIN.
* [x] Les comptes USER peuvent faire login et recevoir un message de bienvenue, mais ils ont pas access aux endpoints admin.
* [x] Mots de passe sont hashés avec BCrypt.
* [x] Bonne séparation du code (model / repository / dto / security / service / controller).
* [x] La gestion des erreurs est fait direct dans les methodes service et controller.

---

## Tech Stack

| Technologie                     | Version |
| ------------------------------- | ------- |
| **Java**                        | 17      |
| **Spring Boot**                 | 4.1.1   |
| **Spring Security**             | —       |
| **Spring Data JPA / Hibernate** | —       |
| **PostgreSQL**                  | —       |
| **JWT**                         | 0.11.5  |
| **Maven**                       | —       |

---

## Structure du projet

```text
src/main/java/com/example/employeeapp
│
├── EmployeeAppApplication.java
├── DataInitializer.java        # crée un admin par defaut au premier lancement
│
├── model/
│   ├── Employee.java
│   └── Role.java
│
├── repository/
│   └── EmployeeRepository.java
│
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterEmployeeRequest.java
│   ├── EmployeeUpdateRequest.java
│   ├── EmployeeResponse.java
│   └── AuthResponse.java
│
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   ├── CustomUserDetailsService.java
│   └── SecurityConfig.java
│
├── service/
│   ├── AuthService.java
│   └── EmployeeService.java
│
└── controller/
    ├── AuthController.java
    └── EmployeeController.java
```

---

# Pour commencer

## Prerequisites

* [x] Java 17+
* [x] Maven 3.8+
* [x] PostgreSQL qui tourne en local (ou accessible)

## Configurer application.properties

```properties
# ===== Server =====
server.port=8080

# ===== Database (PostgreSQL) =====
spring.datasource.url=jdbc:postgresql://localhost:5432/employee_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

# ===== JPA / Hibernate =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ===== JWT =====
jwt.secret=ThisIsMySuperSecretKeyForJwtSigningPleaseChangeIt123456
jwt.expiration=86400000
```

Assure-toi que pom.xml utilise le driver PostgreSQL au lieu de MySQL:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

Au premier lancement, un compte admin par defaut est créé automatiquement:

```text
username: admin
password: admin123
```

---

# API Reference

**Base URL:** `http://localhost:8080`

| Method   | Endpoint                            | Access   | Description                                          |
| -------- | ----------------------------------- | -------- | ---------------------------------------------------- |
| `POST`   | `/api/auth/login`                   | `Public` | Se connecter, recevoir un JWT + message de bienvenue |
| `POST`   | `/api/employees`                    | `ADMIN`  | Créer un nouveau employee (cré aussi son login)      |
| `GET`    | `/api/employees`                    | `ADMIN`  | Lister tous les employees                            |
| `PUT`    | `/api/employees/{id}`               | `ADMIN`  | Modifier les infos de travail d'un employee          |
| `DELETE` | `/api/employees/{id}`               | `ADMIN`  | Supprimer un employee                                |
| `PUT`    | `/api/employees/promote/{username}` | `ADMIN`  | Promouvoir un user en admin                          |

Tous les endpoints protégés demandent le header:

```text
Authorization: Bearer <token>
```

---

## LoginRequest

### HTTP

```http
POST /api/auth/login
Content-Type: application/json
```

### Request

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9....",
  "message": "Welcome admin admin"
}
```

---

## Create employee

### Request

```http
POST /api/employees
Content-Type: application/json
Authorization: Bearer <admin_token>
```

```json
{
  "username": "jdoe",
  "password": "pass123",
  "fullName": "John Doe",
  "position": "Backend Developer",
  "department": "Engineering",
  "salary": 4500.0
}
```

### Response

```json
{
  "id": 2,
  "username": "jdoe",
  "fullName": "John Doe",
  "position": "Backend Developer",
  "department": "Engineering",
  "salary": 4500.0,
  "roles": ["USER"]
}
```

---

## Update employee

```http
PUT /api/employees/2
Content-Type: application/json
Authorization: Bearer <admin_token>
```

```json
{
  "fullName": "John Doe",
  "position": "Senior Backend Developer",
  "department": "Engineering",
  "salary": 5200.0
}
```

---

## Delete employee

```http
DELETE /api/employees/2
Authorization: Bearer <admin_token>
```

---

## Promote a user to admin

```http
PUT /api/employees/promote/jdoe
Authorization: Bearer <admin_token>
```

### Response

```text
"jdoe is now an admin"
```

---

# Gestion des erreurs

Les erreurs sont gérées directement dans les methodes du service et du controller (pas de package global pour les exceptions):

| Situation                                   | Status | Exemple de message                 |
| ------------------------------------------- | -----: | ---------------------------------- |
| Mauvais username/password                   |  `401` | `Invalid username or password`     |
| Token manquant ou invalide                  |  `403` | `(bloqué par Spring Security)`     |
| Username déjà existant à la création        |  `400` | `Username already exists: jdoe`    |
| Employee non trouvé (update/delete/promote) |  `404` | `Employee not found with id: 9999` |

---

<div align="center">

**Spring Boot · Spring Security · JWT · PostgreSQL**

</div>
