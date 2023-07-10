package com.ms.gamemanagement.service.impl;

import com.ms.gamemanagement.exception.DuplicateGameException;
import com.ms.gamemanagement.exception.GameNotFoundException;
import com.ms.gamemanagement.modal.Game;
import com.ms.gamemanagement.service.GameService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import static com.ms.gamemanagement.constants.GameConstants.*;

@Slf4j
@Service
@NoArgsConstructor
public class GameServiceImpl implements GameService {

    private final Map<String, Game> gameCache = new ConcurrentHashMap<>();

    @Override
    public Optional<Game> createGame(Game game) throws DuplicateGameException {
        String gameName = game.getName();
        if (gameCache.containsKey(gameName)) {
            log.warn(GAME_ALREADY_EXISTS, gameName);
            throw new DuplicateGameException("Game already exists with name: " + gameName);
        }
        gameCache.put(gameName, game);
        log.info(CREATED_GAME, game);
        return Optional.of(game);
    }

    @Override
    public Optional<Game> getGame(String name) {
        Game game = gameCache.get(name);
        if (game != null) {
            log.info(RETRIEVED_GAME, game);
            return Optional.of(game);
        }
        log.warn(GAME_NOT_FOUND, name);
        return Optional.empty();
    }

    @Override
    public Optional<Game> updateGame(String name, Game updatedGame) throws GameNotFoundException {
        if (!gameCache.containsKey(name)) {
            log.warn(GAME_NOT_FOUND, name);
            throw new GameNotFoundException("Game not found with name: " + name);
        }

        Game existingGame = gameCache.get(name);
        existingGame.setName(updatedGame.getName());
        existingGame.setCreationDate(updatedGame.getCreationDate());
        existingGame.setActive(updatedGame.isActive());

        log.info(UPDATED_GAME, existingGame);

        return Optional.of(existingGame);
    }

    @Override
    public void deleteGame(String name) throws GameNotFoundException {
        Game removedGame = gameCache.remove(name);
        if (removedGame != null) {
            log.info(DELETED_GAME, removedGame);
        } else {
            log.warn(GAME_NOT_FOUND, name);
            throw new GameNotFoundException("Game not found with name: " + name);
        }
    }

    @Override
    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>(gameCache.values());
        log.info(RETRIEVED_ALL_GAMES, games);
        return games;
    }

    @Override
    public int deleteAllGames() {
        int numDeleted = gameCache.size();
        gameCache.clear();
        log.info(DELETED_ALL_GAMES, numDeleted);
        return numDeleted;
    }

}