# TeamFlow API Contract

Base URL: `/api`  
Auth: `Authorization: Bearer <accessToken>` (except `/api/auth/*`)  
JSON: camelCase  
Date: `yyyy-MM-dd`  
Date-time: ISO-8601 UTC

## Error Envelope

Every error response from every endpoint uses the following envelope:

```json
{
  "timestamp": "2026-09-08T10:12:33Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Invalid data",
  "path": "/api/projects",
  "fieldErrors": [
    {
      "field": "name",
      "message": "Project name must not be empty"
    }
  ]
}

Supported error codes:

VALIDATION_ERROR
UNAUTHORIZED
INVALID_CREDENTIALS
TOKEN_EXPIRED
FORBIDDEN
NOT_FOUND
CONFLICT
STALE_DATA
INTERNAL_ERROR
Page Envelope

Every paginated list endpoint uses the following envelope:

{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
Endpoint List
Auth
Method	URL	Auth	Status
POST	/api/auth/register	Public	201 / 400 / 409
POST	/api/auth/login	Public	200 / 401
POST	/api/auth/refresh	Public	200 / 401
POST	/api/auth/logout	Authenticated	204
User
Method	URL	Auth	Status
GET	/api/users/me	Authenticated	200 / 401
PATCH	/api/users/me	Authenticated	200 / 400
PUT	/api/users/me/password	Authenticated	204 / 400 / 401
Project
Method	URL	Auth	Status
GET	/api/projects	Authenticated	200
POST	/api/projects	Authenticated	201 / 400
GET	/api/projects/{id}	Member	200 / 403 / 404
PUT	/api/projects/{id}	OWNER, MANAGER	200 / 403
DELETE	/api/projects/{id}	OWNER	204 / 403
Member
Method	URL	Auth	Status
GET	/api/projects/{id}/members	Member	200 / 403
POST	/api/projects/{id}/members	OWNER, MANAGER	201 / 403 / 409
PATCH	/api/projects/{id}/members/{userId}	OWNER, MANAGER	200 / 403
DELETE	/api/projects/{id}/members/{userId}	OWNER	204 / 403
Task
Method	URL	Auth	Status
GET	/api/projects/{id}/tasks	Member	200 / 403
POST	/api/projects/{id}/tasks	OWNER, MANAGER	201 / 400 / 403
GET	/api/tasks/{taskId}	Member	200 / 403 / 404
PUT	/api/tasks/{taskId}	OWNER, MANAGER, creator	200 / 400 / 403 / 409
PATCH	/api/tasks/{taskId}/status	Member	200 / 403 / 409
DELETE	/api/tasks/{taskId}	OWNER, MANAGER	204 / 403
GET	/api/tasks/my	Authenticated	200
Dashboard
Method	URL	Auth	Status
GET	/api/projects/{id}/stats	Member	200 / 403
GET	/api/dashboard/summary	Authenticated	200
Detailed Endpoint Contracts
POST /api/auth/register
Auth

No authentication required.

Request
{
  "email": "tai@teamflow.dev",
  "password": "SuperSecret123",
  "fullName": "Le Hoang Minh Tai"
}
Response

201 Created

{
  "id": 1,
  "email": "tai@teamflow.dev",
  "fullName": "Le Hoang Minh Tai",
  "avatarUrl": null,
  "createdAt": "2026-09-08T03:12:44Z"
}
Errors

400 VALIDATION_ERROR

Invalid email format
Password is shorter than 8 characters
Full name is empty

409 CONFLICT

Email already exists
Constraints
The response MUST NEVER contain password or passwordHash.
Email must be trimmed and stored in lowercase.
Password length must be between 8 and 72 characters.
```
