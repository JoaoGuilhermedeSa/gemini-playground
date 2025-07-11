# Character Creation API

This is a simple Spring Boot application for managing character creation.

## How to Run the Application

To run this application, you need to have Java 17 and Maven installed.

1.  **Build the project**:
    ```bash
    mvn clean install
    ```
2.  **Run the application**:
    ```bash
    java -jar target/character-creation-0.0.1-SNAPSHOT.jar
    ```
    The application will start on port 8080 by default.

## API Endpoints

### User Management (Authentication not required)

#### 1. Create Account
-   **URL**: `/users/create-account`
-   **Method**: `POST`
-   **Description**: Registers a new user account.
-   **Request Body**:
    ```json
    {
        "username": "your_username",
        "password": "your_password"
    }
    ```
-   **Response**:
    ```json
    {
        "jwt": "your_jwt_token",
        "expiresAt": 1678886400000
    }
    ```

#### 2. Login
-   **URL**: `/users/login`
-   **Method**: `POST`
-   **Description**: Authenticates a user and returns a JWT token.
-   **Request Body**:
    ```json
    {
        "username": "your_username",
        "password": "your_password"
    }
    ```
-   **Response**:
    ```json
    {
        "jwt": "your_jwt_token",
        "expiresAt": 1678886400000
    }
    ```

### Authenticated Endpoints (Requires JWT Token)

For the following endpoints, you need to include the JWT token obtained from `/users/login` or `/users/create-account` in the `Authorization` header as a Bearer token:

`Authorization: Bearer <your_jwt_token>`

#### 1. Get Account Details
-   **URL**: `/accounts`
-   **Method**: `GET`
-   **Description**: Retrieves details of the authenticated user's account.
-   **Authentication**: Required
-   **Response**:
    ```json
    {
        "id": 1,
        "username": "testuser",
        "password": "$2a$10:..." // Hashed password
    }
    ```

#### 2. Create Character
-   **URL**: `/characters`
-   **Method**: `POST`
-   **Description**: Creates a new character for the authenticated user.
-   **Authentication**: Required
-   **Request Body**:
    ```json
    {
        "name": "CharacterName",
        "vocation": "Warrior",
        "characterClass": "Knight"
    }
    ```
-   **Response**:
    ```json
    {
        "id": 1,
        "name": "CharacterName",
        "vocation": "Warrior",
        "characterClass": "Knight",
        "comment": null,
        "markedForDeletion": false
    }
    ```

#### 3. Edit Character Comment
-   **URL**: `/characters/{characterId}`
-   **Method**: `PUT`
-   **Description**: Edits the comment for a specific character.
-   **Authentication**: Required
-   **Path Variable**: `characterId` (ID of the character to edit)
-   **Request Body**:
    ```json
    {
        "comment": "New comment for the character."
    }
    ```
-   **Response**:
    ```json
    {
        "id": 1,
        "name": "CharacterName",
        "vocation": "Warrior",
        "characterClass": "Knight",
        "comment": "New comment for the character.",
        "markedForDeletion": false
    }
    ```

#### 4. Mark Character for Deletion
-   **URL**: `/characters/{characterId}`
-   **Method**: `DELETE`
-   **Description**: Marks a character for deletion.
-   **Authentication**: Required
-   **Path Variable**: `characterId` (ID of the character to mark for deletion)
-   **Response**: No content (200 OK)

## Authentication

This application uses JSON Web Tokens (JWT) for authentication.

1.  **Obtain a JWT**:
    -   Make a `POST` request to `/users/create-account` to register and get a token, or to `/users/login` if you already have an account.
    -   The response will contain a `jwt` field with your token.

2.  **Use the JWT**:
    -   Include the obtained JWT in the `Authorization` header of your subsequent requests to protected endpoints.
    -   The format should be: `Authorization: Bearer <your_jwt_token>`

**Example using `curl`:**

```bash
# 1. Create an account (or login)
curl -X POST http://localhost:8080/users/create-account \
-H "Content-Type: application/json" \
-d '{"username": "myuser", "password": "mypassword"}'

# (Assume the above returns {"jwt": "eyJ..."})

# 2. Use the JWT to access a protected endpoint (e.g., create a character)
curl -X POST http://localhost:8080/characters \
-H "Content-Type: application/json" \
-H "Authorization: Bearer eyJ..." \
-d '{"name": "MyNewCharacter", "vocation": "Mage", "characterClass": "Wizard"}'

# 3. Get account details
curl -X GET http://localhost:8080/accounts \
-H "Authorization: Bearer eyJ..."
```