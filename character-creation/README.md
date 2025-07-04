# Character Creation API

This is a Spring Boot application that provides a RESTful API for character creation in an MMORPG.

## Endpoints

The following endpoints are available:

### Accounts

*   `POST /accounts`: Creates a new account.

    **Request Body:**

    ```json
    {
        "username": "your_username",
        "password": "your_password"
    }
    ```

*   `POST /accounts/login`: Authenticates an account.

    **Request Body:**

    ```json
    {
        "username": "your_username",
        "password": "your_password"
    }
    ```

### Characters

*   `POST /characters/{accountId}`: Creates a new character for the specified account.

    **Request Body:**

    ```json
    {
        "name": "character_name",
        "vocation": "character_vocation",
        "characterClass": "character_class"
    }
    ```

*   `PUT /characters/{characterId}`: Edits the comment of a character.

    **Request Body:**

    ```json
    {
        "comment": "new_comment"
    }
    ```

*   `DELETE /characters/{characterId}`: Marks a character for deletion. The character will be deleted after 30 days.

## How to Run

1.  Clone the repository.
2.  Make sure you have Java 17 and Maven installed.
3.  Navigate to the project root directory.
4.  Run the application using the following command:

    ```bash
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`.
