# Security Microservice API Documentation

This document outlines all exposed REST APIs for the Security Service, including authentication paths, user management, and authorization test endpoints.

All responses are wrapped in a standard `ApiResponse` generic envelope:
```json
{
  "success": true,
  "message": "Human readable message",
  "data": { ... },
  "timestamp": "2026-03-30T21:00:00.000000"
}
```

---

## 1. Authentication Endpoints
**Base Path:** `/api/v1/auth`  
**Security:** Public

### 1.1 Login (Local)
Authenticate with a username or email and password.

- **URL:** `/login`
- **Method:** `POST`
- **Request Body:**
```json
{
  "usernameOrEmail": "johndoe",
  "password": "mySecurePassword123"
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800000,
    "user": {
      "id": 1,
      "name": "John Doe",
      "username": "johndoe",
      "email": "john@example.com",
      "active": true,
      "provider": "LOCAL",
      "roles": ["ROLE_USER"]
    }
  }
}
```
- **Error Responses:** `401 Unauthorized` (Invalid credentials), `400 Bad Request` (Validation error).

### 1.2 Register
Create a new local user account.

- **URL:** `/register`
- **Method:** `POST`
- **Request Body:**
```json
{
  "name": "Jane Doe",
  "username": "janedoe99",
  "email": "jane@example.com",
  "password": "Password!123"
}
```
- **Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful! Please login to continue.",
  "data": "janedoe99"
}
```
- **Error Responses:** `409 Conflict` (Username or email already exists), `400 Bad Request` (Validation error).

### 1.3 Validate Token
Verify if a given JWT token is active and valid (used by other microservices internally).

- **URL:** `/validate`
- **Method:** `POST`
- **Request Body:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Token is valid",
  "data": {
    "valid": true,
    "username": "johndoe",
    "message": "Token is valid"
  }
}
```

---

## 2. OAuth2 / Social Login Endpoints
Handled automatically by Spring Security.

### 2.1 Login With Google
Redirects the user directly to the Google Consent Screen.
- **URL:** `GET /oauth2/authorization/google`
- **Response:** Browser redirect to frontend on success parameterizing the JWT `http://localhost:5173/oauth2/callback?token=xxx&type=Bearer`

### 2.2 Login With GitHub
Redirects the user directly to the GitHub Consent Screen.
- **URL:** `GET /oauth2/authorization/github`
- **Response:** Browser redirect to frontend on success parameterizing the JWT `http://localhost:5173/oauth2/callback?token=xxx&type=Bearer`

---

## 3. User Management Endpoints
**Base Path:** `/api/v1/users`  
**Security:** Requires Bearer Token (`Authorization: Bearer <token>`)

### 3.1 Get Profile (Self)
Fetch the profile of the currently logged-in user.
- **URL:** `/me`
- **Method:** `GET`
- **Security:** `ROLE_USER` or `ROLE_ADMIN`
- **Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile fetched successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "username": "johndoe",
    "provider": "GOOGLE",
     ...
  }
}
```

### 3.2 Get All Users (Admin)
- **URL:** `/`
- **Method:** `GET`
- **Security:** `ROLE_ADMIN` ONLY
- **Success Response:** `data` contains `[UserDto, UserDto, ...]`

### 3.3 Get Single User (Admin)
- **URL:** `/{id}`
- **Method:** `GET`
- **Security:** `ROLE_ADMIN` ONLY
- **Success Response:** `data` contains a specific `UserDto`

### 3.4 Update User (Admin)
Update any aspect of a user account, including assigning roles.
- **URL:** `/{id}`
- **Method:** `PUT`
- **Security:** `ROLE_ADMIN` ONLY
- **Request Body:** (All fields optional)
```json
{
  "name": "New Name",
  "email": "new@example.com",
  "active": true,
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```
- **Success Response:** `data` contains the patched `UserDto`

### 3.5 Delete User (Admin)
- **URL:** `/{id}`
- **Method:** `DELETE`
- **Security:** `ROLE_ADMIN` ONLY
- **Success Response (200 OK):** `data` is null

### 3.6 Get User Count (Admin)
- **URL:** `/count`
- **Method:** `GET`
- **Security:** `ROLE_ADMIN` ONLY
- **Success Response:** `data` contains numeric count of registered users.

---

## 4. Demo / Authority Testing Endpoints
**Base Path:** `/api/v1/demo`  
Built-in sandbox endpoints to test role constraints securely.

### 4.1 Public Test
- **URL:** `/public`
- **Method:** `GET`
- **Security:** Open
- **Response:** "Hello! This is a public endpoint."

### 4.2 User Test
- **URL:** `/user`
- **Method:** `GET`
- **Security:** `ROLE_USER` or `ROLE_ADMIN`
- **Response:** "Hello {username}! You have accessed a protected USER endpoint."

### 4.3 Admin Test
- **URL:** `/admin`
- **Method:** `GET`
- **Security:** `ROLE_ADMIN` ONLY
- **Response:** "Hello Admin {username}! You have accessed a highly protected ADMIN endpoint."
