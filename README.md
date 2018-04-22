# jwt-spring-webflux

Sample project that uses Spring Webflux with JWT Tokens

## Run the application
```
gradlew bootRun
```

## Endpoints
All requests might need the media header
```header
Content-Type: application/json
```

### Tokens
POST http://localhost:8080/oauth/token

Body
```json
{
  "username": "John"
}
```

Sample response
```json
{
    "token": "ey...",
    "expiresIn": 7200
}
```

### Protected Routes
GET http://localhost:8080/secure

Headers
```headers
Authorization: Bearer {token}
```


Sample response
```
Hello John!
```