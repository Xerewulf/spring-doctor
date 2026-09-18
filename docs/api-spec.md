# DevTools AI REST API Specification

Base URL: `/api/v1`

All responses conform to a unified wrapper structure:
```json
{
  "success": true,
  "data": { ... },
  "message": "Optional descriptive status",
  "errorCode": null,
  "timestamp": "2026-09-18T17:00:00"
}
```

---

## 1. Authentication Endpoints

### Register User
- **Method**: `POST /auth/register`
- **Auth**: Public
- **Request Body**:
```json
{
  "email": "alex@company.com",
  "password": "SecurePassword123!",
  "firstName": "Alex",
  "lastName": "Developer"
}
```
- **Response**: `201 Created`
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGci...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "email": "alex@company.com",
      "firstName": "Alex",
      "lastName": "Developer",
      "plan": "FREE",
      "roles": ["ROLE_USER"]
    }
  }
}
```

### Login
- **Method**: `POST /auth/login`
- **Auth**: Public
- **Request Body**:
```json
{
  "email": "alex@company.com",
  "password": "SecurePassword123!"
}
```
- **Response**: `200 OK`

### Get Current User Profile
- **Method**: `GET /auth/me`
- **Auth**: Bearer Token required
- **Response**: `200 OK`

---

## 2. Error Doctor Analysis

### Analyze Error
- **Method**: `POST /analysis/error`
- **Auth**: Optional (Supports anonymous and authenticated users)
- **Request Body**:
```json
{
  "errorText": "org.hibernate.LazyInitializationException: could not initialize proxy - no Session...",
  "technology": "SPRING_BOOT",
  "context": "Calling GET /customers/123",
  "saveInput": false
}
```
- **Response**: `200 OK`
```json
{
  "success": true,
  "data": {
    "summary": "Your application is attempting to access a lazily loaded Hibernate collection after the persistence session has closed.",
    "rootCause": "Hibernate Session closed before proxy initialization.",
    "confidence": "HIGH",
    "severity": "MEDIUM",
    "whyItHappens": "Lazy proxies require an active database Session to load data on demand...",
    "suggestedFixes": [
      {
        "title": "Fetch Join using @EntityGraph",
        "description": "Explicitly join-fetch the relationship in the Spring Data repository.",
        "code": "@EntityGraph(attributePaths = {\"orders\"})\nOptional<User> findById(Long id);",
        "language": "java"
      }
    ],
    "thingsToCheck": [
      "Check whether the entity is accessed outside the service @Transactional boundary."
    ],
    "relatedTechnologies": ["Spring Boot", "Hibernate"],
    "possibleCauses": ["Direct entity serialization by Jackson."],
    "detectedException": "LazyInitializationException",
    "sanitizationNotice": null,
    "analysisId": 12,
    "wasInputSaved": false
  }
}
```

---

## 3. History & Dashboard

### Get User Analysis History
- **Method**: `GET /analysis/history?page=0&size=20`
- **Auth**: Bearer Token required

### Get Analysis Detail
- **Method**: `GET /analysis/{id}`
- **Auth**: Bearer Token required

### Delete Analysis
- **Method**: `DELETE /analysis/{id}`
- **Auth**: Bearer Token required

### Get Dashboard Stats
- **Method**: `GET /dashboard/stats`
- **Auth**: Bearer Token required

---

## 4. Usage Quota & Billing

### Inspect Daily Usage
- **Method**: `GET /usage`
- **Auth**: Public (inspects client IP or user ID)
- **Response**:
```json
{
  "success": true,
  "data": {
    "plan": "FREE",
    "dailyLimit": 10,
    "usedToday": 3,
    "remainingToday": 7,
    "authenticated": true
  }
}
```

### Upgrade Plan
- **Method**: `POST /billing/upgrade`
- **Auth**: Bearer Token required
- **Request Body**: `{"plan": "PRO"}`
