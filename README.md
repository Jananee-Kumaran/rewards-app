# Rewards App

This project provides a small Spring Boot service that calculates reward points for customers based on their transaction history.  
The application follows the rule set given in the assignment:

- No points for the first $50.
- $1 = 1 point for amounts between $50 and $100.
- $1 = 2 points for every dollar spent above $100.

Example:  
A transaction of $120 → 90 points.

The app exposes two APIs:
1. Fetch reward summary for a single customer (last 3 months)
2. Fetch reward summaries for all customers (last 3 months)

The project uses an in-memory H2 database with sample data loaded through `data.sql`.

## API Endpoints

### 1. Rewards for a single customer (last 3 months)

```
GET /api/rewards/customer/{customerId}/three-months
```

```
curl -X GET "http://localhost:8080/api/rewards/customer/3/three-months" -H "accept: application/json"
```

### 2. Rewards for all customers (last 3 months)

```
GET /api/rewards/summary/three-months
```

```
curl -X GET "http://localhost:8080/api/rewards/summary/three-months" -H "accept: application/json"
```
---

## Swagger

Swagger UI is available here:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Project Structure

```
src/main/java/com/rewards/points
 ├── controller
 ├── service
 ├── repository
 ├── dto
 ├── builder
 ├── model
 └── exception

src/test/java/com/rewards/points
 ├── controller tests
 └── service tests
```

---

##Tech stack

Java 17
Spring Boot 3
Spring Web
Spring Data JPA
H2 In-Memory Database
JUnit 5 / Mockito
Swagger

## Reward Calculation

All reward calculations are handled in `RewardPointsCalculator`.  
Transactions are grouped month-wise and aggregated before building the final response.

Each API returns:
- Customer details  
- Points earned per month  
- Total points  
- List of transactions with calculated points  

Example response:

```json
{
  "customerId": 3,
  "customerName": "Bob Johnson",
  "monthlyPoints": {
    "NOVEMBER": 90,
    "OCTOBER": 30
  },
  "totalPoints": 120,
  "transactions": [
    {
      "id": 9,
      "date": "2025-11-21",
      "amount": 120.0,
      "points": 90
    }
  ]
}
```

---

## Tests

The project contains:
- Service tests (reward calculation logic)
- Controller tests (MockMvc)

Run all tests with:

```bash
mvn test
```

---

## Notes

- DTOs are separated from JPA entities.
- Responses are built using a small builder helper to keep the service code clean.
