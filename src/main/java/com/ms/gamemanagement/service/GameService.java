package com.ms.gamemanagement.service;
import com.ms.gamemanagement.exception.DuplicateGameException;
import com.ms.gamemanagement.exception.GameNotFoundException;
import com.ms.gamemanagement.modal.Game;

import java.util.List;
import java.util.Optional;

public interface GameService {
    /**
     * Creates a new game.
     *
     * @param game the game to create
     * @return an optional containing the created game if successful, or an empty optional if a game with the same name already exists
     * @throws DuplicateGameException if a game with the same name already exists
     */
    Optional<Game> createGame(Game game) throws DuplicateGameException;

    /**
     * Retrieves a game by its name.
     *
     * @param name the name of the game to retrieve
     * @return an optional containing the retrieved game if found, or an empty optional if the game does not exist
     */
    Optional<Game> getGame(String name);

    /**
     * Updates an existing game.
     *
     * @param name        the name of the game to update
     * @param updatedGame the updated game object
     * @throws GameNotFoundException if the game to be updated does not exist
     * @return an optional containing the updated game if successful, or an empty optional if the game does not exist
     */
    Optional<Game> updateGame(String name, Game updatedGame) throws GameNotFoundException;

    /**
     * Deletes a game by its name.
     *
     * @param name the name of the game to delete
     * @throws GameNotFoundException if the game to be deleted does not exist
     */
    void deleteGame(String name) throws GameNotFoundException;

    /**
     * Retrieves a list of all games.
     *
     * @return A list of games. If no games are found, an empty list is returned.
     */
    List<Game> getAllGames();

    /**
     * Deletes all games.
     *
     * @return the number of games deleted
     */
    int deleteAllGames();
}
