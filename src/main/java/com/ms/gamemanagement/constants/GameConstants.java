package com.ms.gamemanagement.constants;

public class GameConstants {

    // Path mappings
    public static final String BASE_PATH = "/v1/games";
    public static final String GAME_PATH = "/{name}";

    // HTTP status codes
    public static final int STATUS_OK = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_NO_CONTENT = 204;

    // Log messages
    public static final String LOG_CREATED_GAME = "Created game: {}";
    public static final String LOG_GAME_ALREADY_EXISTS = "Game already exists with name: {}";
    public static final String LOG_RETRIEVED_GAME = "Retrieved game: {}";
    public static final String LOG_UPDATED_GAME = "Updated game: {}";
    public static final String LOG_GAME_NOT_FOUND = "Game not found with name: {}";
    public static final String LOG_DELETED_GAME = "Deleted game with name: {}";
    public static final String LOG_ERROR_CREATING_GAME = "Error creating game: {}";
    public static final String LOG_ERROR_DELETING_GAME = "Error deleting game: {}";
    public static final String LOG_RETRIEVED_ALL_GAMES = "Retrieved all games: {}";
    public static final String LOG_DELETED_ALL_GAMES = "Deleted all games. Count: {}";

//  Game Service Constants

    public static final String GAME_ALREADY_EXISTS = "Game already exists with name: %s";
    public static final String GAME_NOT_FOUND = "Game not found with name: %s";
    public static final String CREATED_GAME = "Created game: %s";
    public static final String RETRIEVED_GAME = "Retrieved game: %s";
    public static final String UPDATED_GAME = "Updated game: %s";
    public static final String DELETED_GAME = "Deleted game: %s";
    public static final String DELETED_ALL_GAMES = "Deleted all games. Count: %d";
    public static final String ERROR_CREATING_GAME = "Error creating game: %s";
    public static final String ERROR_DELETING_GAME = "Error deleting game: %s";
    public static final String RETRIEVED_ALL_GAMES = "Retrieved all games: %s";


}
