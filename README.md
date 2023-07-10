# Game Management Application

The Game Management Application is a Java-based application developed using Spring and Maven. It provides APIs for managing games within a game service provider environment.

## Features

- Create a new game with a unique name, creation date, and active status.
- Retrieve information about a specific game based on its name.
- Retrieve all games 
- Update the details of an existing game.
- Delete a game from the system.
- Delete all games from the system.

## Technologies Used

- Java 20
- Springboot 3.1.1
- Maven

## Getting Started

To run the Game Management Application locally, follow these steps:

1. Clone the repository: `git clone <repository-url>`
2. Navigate to the project directory: `cd game-management`
3. Build the project using Maven: `mvn clean install`
4. Start the application: `java -jar target/game-management-1.0.0.jar`

The application will start running on `http://localhost:8080`.

## API Documentation

The API documentation is generated using springdoc openapi. You can access the API documentation by visiting the following URL in your browser:

- http://localhost:8080/swagger-ui.html
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/v3/api-docs

## Actuator endpoints configuration

- Health: http://localhost:8080/actuator/health



