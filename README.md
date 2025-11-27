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
  "startDate": "2024-01-01",
  "endDate": "2024-03-01"
}
```

#### Sample Response  
```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "monthlyPoints": {
    "JANUARY": 90,
    "FEBRUARY": 30
  },
  "totalPoints": 120,
  "totalAmount": 240.0,
  "transactions": [
    {
      "id": 101,
      "date": "2024-01-10",
      "amount": 120.0,
      "points": 90
    },
    {
      "id": 102,
      "date": "2024-02-15",
      "amount": 80.0,
      "points": 30
    }
  ]
}
```

---

## Get rewards for the default last 3 months  
### **GET /api/rewards/customer/{customerId}**

#### Example  
```
GET /api/rewards/customer/3
```

#### Sample Response  
```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "monthlyPoints": {
    "DECEMBER": 60,
    "JANUARY": 90,
    "FEBRUARY": 30
  },
  "totalPoints": 180,
  "totalAmount": 350.0,
  "transactions": [
    {
      "id": 201,
      "date": "2024-12-20",
      "amount": 100.0,
      "points": 50
    },
    {
      "id": 202,
      "date": "2025-01-10",
      "amount": 120.0,
      "points": 90
    },
    {
      "id": 203,
      "date": "2025-02-10",
      "amount": 80.0,
      "points": 30
    }
  ]
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
