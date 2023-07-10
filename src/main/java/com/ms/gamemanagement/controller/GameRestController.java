package com.ms.gamemanagement.controller;

import com.ms.gamemanagement.exception.DuplicateGameException;
import com.ms.gamemanagement.exception.GameNotFoundException;
import com.ms.gamemanagement.modal.Game;
import com.ms.gamemanagement.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static com.ms.gamemanagement.constants.GameConstants.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/games")
@Tag(name = "Games Controller", description = "API endpoints for managing games")
public class GameRestController {

    private final GameService gameService;

    @PostMapping
    @Operation(summary = "Create a new game")
    public ResponseEntity<Game> createGame(@Validated @RequestBody Game game) {
        try {
            Optional<Game> createdGameOptional = gameService.createGame(game);

            if (createdGameOptional.isPresent()) {
                Game createdGame = createdGameOptional.get();
                log.info(LOG_CREATED_GAME, createdGame);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
            } else {
                log.warn(LOG_GAME_ALREADY_EXISTS, game.getName());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (DuplicateGameException e) {
            log.error(LOG_ERROR_CREATING_GAME, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @GetMapping(GAME_PATH)
    @Operation(summary = "Get a game by name")
    public ResponseEntity<Game> getGame(
            @Parameter(description = "Name of the game", required = true)
            @PathVariable String name ) {
        return gameService.getGame(name)
                .map(game -> {
                    // Game found
                    log.info(LOG_RETRIEVED_GAME, game);
                    return ResponseEntity.ok(game);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(GAME_PATH)
    @Operation(summary = "Update a game by name")
    public ResponseEntity<Game> updateGame(
            @Parameter(description = "Name of the game", required = true)
            @PathVariable String name,
            @Validated @RequestBody Game updatedGame) {
        try {
            Optional<Game> updatedGameResult = gameService.updateGame(name, updatedGame);
            if (updatedGameResult.isPresent()) {
                log.info(LOG_UPDATED_GAME, updatedGameResult.get());
                return ResponseEntity.ok(updatedGameResult.get());
            } else {
                log.warn(LOG_GAME_NOT_FOUND, name);
                return ResponseEntity.notFound().build();
            }
        } catch (GameNotFoundException e) {
            log.warn(LOG_GAME_NOT_FOUND, name);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(GAME_PATH)
    @Operation(summary = "Delete a game by name")
    public ResponseEntity<Void> deleteGame(
            @Parameter(description = "Name of the game", required = true)
            @PathVariable String name)  {
        try {
            gameService.deleteGame(name);
            // Game deletion successful
            log.info(LOG_DELETED_GAME, name);
            return ResponseEntity.noContent().build();
        } catch (GameNotFoundException e) {
            log.error(LOG_ERROR_DELETING_GAME, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all games")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        log.info(LOG_RETRIEVED_ALL_GAMES, games);
        return ResponseEntity.ok(games);
    }

    @DeleteMapping
    @Operation(summary = "Delete all games")
    public ResponseEntity<Void> deleteAllGames() {
        int numDeleted = gameService.deleteAllGames();
        log.info(LOG_DELETED_ALL_GAMES, numDeleted);
        return ResponseEntity.noContent().build();
    }

}
