# Rewards App

This project provides a Spring Boot service for calculating customer reward points based on transaction history.  
It supports **two operations**:

1. **POST API** – Calculate rewards for a *specific date range*  
2. **GET API** – Calculate rewards for the *most recent three months* (default behavior)

The application uses an H2 in‑memory database with sample data populated via `data.sql`.

---

# Reward Rules

- No points for the first **$50**
- **1 point** per dollar between **$50–$100**
- **2 points** per dollar above **$100**

**Example:**  
A transaction of **$120 → 90 points**

---

# API Endpoints

## Calculate rewards for a date range  
### **POST /api/rewards/calculate**

#### Sample Request  
```json
{
  "customerId": 1,
  "startDate": "2025-09-02",
  "endDate": "2025-12-02"
}
```

#### Sample Response  
```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "monthlyPoints": [
    { "year": 2024, "month": "JANUARY", "points": 90 },
    { "year": 2024, "month": "FEBRUARY", "points": 30 }
  ],
  "totalPoints": 120,
  "totalAmount": 240.0
}
```

---

## Get rewards for the default last 3 months  
### **GET /api/rewards/customer/{customerId}**

#### Example  
```
GET /api/rewards/customer/2
```

#### Sample Response  
```json
{
  "customerId": 2,
  "customerName": "Alice Smith",
  "monthlyPoints": [
    {
      "year": 2025,
      "month": "DECEMBER",
      "points": 25,
      "amount": 75
    },
    {
      "year": 2025,
      "month": "NOVEMBER",
      "points": 45,
      "amount": 95
    },
    {
      "year": 2025,
      "month": "OCTOBER",
      "points": 110,
      "amount": 130
    }
],
  "totalPoints": 180,
  "totalAmount": 300
}
```

---

# Project Structure

```
src/main/java/com/rewards/app
 ├── controller
 ├── service
 ├── repository
 ├── dto
 ├── model
 └── exception

src/test/java/com/rewards/app
 ├── controller
 └── service
```

---

# Tech Stack

- Java 17  
- Spring Boot 3  
- Spring Web  
- Spring Data JPA  
- H2 Database  
- Lombok  
- Jakarta Validation  
- JUnit 5 + Mockito  
- Springdoc OpenAPI (Swagger)

---

#  Swagger UI  
```
http://localhost:8080/swagger-ui/index.html
```

---

#  Running Tests  
```
mvn test
```

Tests include:

- Controller tests (MockMvc)
- Service tests with boundary values
- Validation tests
- Error handling tests


# Summary

| API | Purpose | Date Range |

| **POST /api/rewards/calculate** | User-provided calculation | Requires startDate + endDate |

| **GET /api/rewards/customer/{id}** | Auto last 3 months | No params allowed |
