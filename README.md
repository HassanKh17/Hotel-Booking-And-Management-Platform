# Hotel Booking and Management Platform

A full-stack hotel booking and management system with customer booking flows, hotel owner management, admin reporting, reviews, room availability, saved cards, and booking confirmation email support.

## Project Structure

- `hotel-booking-system-frontend` - React frontend
- `HotelManagementSystemBackend` - Spring Boot REST API
- `diagrams` - ERD and UML documentation

## Tech Stack

- React, React Router, Axios, Bootstrap
- Java 17, Spring Boot, Spring Security, Spring Data JPA
- H2 for local/demo persistence
- Maven wrapper for backend builds

## Local Setup

### Backend

```bash
cd HotelManagementSystemBackend
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

The backend runs at:

```text
http://localhost:8080/api/v1
```

### Frontend

```bash
cd hotel-booking-system-frontend
npm install
npm start
```

Create `hotel-booking-system-frontend/.env.local` if needed:

```text
REACT_APP_API_URL=http://localhost:8080/api/v1
```

## Deployment Notes

Recommended deployment:

- Frontend: Vercel
- Backend: Render
- Database: H2 for demo, or PostgreSQL for a more permanent deployment

Frontend production env var:

```text
REACT_APP_API_URL=https://your-render-service.onrender.com/api/v1
```

Backend production env vars:

```text
APP_CORS_ALLOWED_ORIGINS=https://your-vercel-site.vercel.app
BOOKING_EMAIL_ENABLED=false
```

## Build Checks

Frontend:

```bash
cd hotel-booking-system-frontend
npm run build
```

Backend:

```bash
cd HotelManagementSystemBackend
./mvnw clean package -DskipTests
```