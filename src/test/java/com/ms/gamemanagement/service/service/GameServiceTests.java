package com.ms.gamemanagement.service.service;

import com.ms.gamemanagement.exception.DuplicateGameException;
import com.ms.gamemanagement.exception.GameNotFoundException;
import com.ms.gamemanagement.modal.Game;
import com.ms.gamemanagement.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class GameServiceTests {

	@Autowired
	private GameService gameService;

	@BeforeEach
	void setup() {
		// ensures all games are defined at start
		gameService.deleteAllGames();
	}

	@Test
	@DisplayName("Create and retrieve game")
	void createAndRetrieveGame() {
		// Arrange
		Game game = new Game("Chess", LocalDate.now(), true);

		// Act
		try {
			gameService.createGame(game);
			Optional<Game> retrievedGameOptional = gameService.getGame("Chess");
			Game retrievedGame = retrievedGameOptional.orElse(null);

			// Assert
			assertNotNull(retrievedGame);
			assertEquals(game.getName(), retrievedGame.getName());
			assertEquals(game.getCreationDate(), retrievedGame.getCreationDate());
			assertEquals(game.isActive(), retrievedGame.isActive());
		} catch (DuplicateGameException e) {
			fail("Unexpected DuplicateGameException thrown");
		}
	}

	@Test
	@DisplayName("Update game status")
	void updateGameStatus() {

		// Arrange
		Game initialGame = new Game("Checkers", LocalDate.now(), true);
		gameService.createGame(initialGame);
		Game updatedGame = new Game("Checkers", LocalDate.now(), false);

		// Act
		gameService.updateGame("Checkers", updatedGame);
		Optional<Game> retrievedGameOptional = gameService.getGame("Checkers");
		Game retrievedGame = retrievedGameOptional.orElse(null);

		// Assert
		assertNotNull(retrievedGame);
		assertEquals(updatedGame.isActive(), retrievedGame.isActive());
	}

	@Test
	@DisplayName("Delete game")
	void deleteGame() {
		// Arrange
		Game game = new Game("Darts", LocalDate.now(), true);
		gameService.createGame(game);

		// Act
		gameService.deleteGame("Darts");
		Optional<Game> retrievedGameOptional = gameService.getGame("Darts");

		// Assert
		assertTrue(retrievedGameOptional.isEmpty());
	}

	@Test
	@DisplayName("Delete non-existent game")
	void deleteNonExistentGame() {
		// Arrange
		String nonExistentGameName = "NonExistentGame";

		// Act and Assert
		assertThrows(GameNotFoundException.class, () -> gameService.deleteGame(nonExistentGameName));
	}

	@Test
	@DisplayName("Retrieve non-existent game")
	void retrieveNonExistentGame() {
		// Act
		Optional<Game> retrievedGameOptional = gameService.getGame("NonExistentGame");

		// Assert
		assertTrue(retrievedGameOptional.isEmpty());
	}

	@Test
	@DisplayName("Update non-existent game")
	void updateNonExistentGame() {
		// Arrange
		Game updatedGame = new Game("NonExistentGame", LocalDate.now(), true);

		// Act and Assert
		assertThrows(GameNotFoundException.class, () -> gameService.updateGame("NonExistentGame", updatedGame));
	}

	@Test
	@DisplayName("Create duplicate game")
	void createDuplicateGame() {
		// Arrange
		Game game = new Game("DuplicateGame", LocalDate.now(), true);

		// Act and Assert
		assertThrows(DuplicateGameException.class, () -> {
			gameService.createGame(game);
			gameService.createGame(game);
		});
	}

	@Test
	@DisplayName("Update existing game with partial changes")
	void updateExistingGameWithPartialChanges() {
		// Arrange
		Game initialGame = new Game("Chess", LocalDate.now(), true);
		gameService.createGame(initialGame);

		Game updatedGame = new Game("Chess", LocalDate.now(), false); // Only updating the active status

		// Act
		gameService.updateGame("Chess", updatedGame);
		Optional<Game> retrievedGameOptional = gameService.getGame("Chess");
		Game retrievedGame = retrievedGameOptional.orElse(null);

		// Assert
		assertNotNull(retrievedGame);
		assertEquals(updatedGame.isActive(), retrievedGame.isActive());
		assertEquals(initialGame.getName(), retrievedGame.getName()); // Name should remain unchanged
		assertEquals(initialGame.getCreationDate(), retrievedGame.getCreationDate()); // Creation date should remain unchanged
	}

	@Test
	@DisplayName("Create and retrieve multiple games")
	void createAndRetrieveMultipleGames() {
		// Arrange
		Game game1 = new Game("Chess", LocalDate.now(), true);
		Game game2 = new Game("Checkers", LocalDate.now(), true);
		gameService.createGame(game1);
		gameService.createGame(game2);

		// Act
		Optional<Game> retrievedGame1Optional = gameService.getGame("Chess");
		Optional<Game> retrievedGame2Optional = gameService.getGame("Checkers");

		// Assert
		assertTrue(retrievedGame1Optional.isPresent());
		assertTrue(retrievedGame2Optional.isPresent());
		Game retrievedGame1 = retrievedGame1Optional.get();
		Game retrievedGame2 = retrievedGame2Optional.get();
		assertEquals(game1.getName(), retrievedGame1.getName());
		assertEquals(game1.getCreationDate(), retrievedGame1.getCreationDate());
		assertEquals(game1.isActive(), retrievedGame1.isActive());
		assertEquals(game2.getName(), retrievedGame2.getName());
		assertEquals(game2.getCreationDate(), retrievedGame2.getCreationDate());
		assertEquals(game2.isActive(), retrievedGame2.isActive());
	}

	@Test
	@DisplayName("Concurrent access to game cache")
	void concurrentAccessToGameCache() throws InterruptedException {
		// Arrange
		int numThreads = 10;
		Thread[] threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			threads[i] = new Thread(() -> {
				// Create a unique game for each thread
				Game game = new Game(Thread.currentThread().getName(), LocalDate.now(), true);
				gameService.createGame(game);
				Optional<Game> retrievedGameOptional = gameService.getGame(game.getName());

				// Assert within each thread
				assertTrue(retrievedGameOptional.isPresent());
				Game retrievedGame = retrievedGameOptional.get();
				assertEquals(game.getName(), retrievedGame.getName());
				assertEquals(game.getCreationDate(), retrievedGame.getCreationDate());
				assertEquals(game.isActive(), retrievedGame.isActive());
			});
			threads[i].start();
		}

		// Wait for all threads to complete
		for (Thread thread : threads) {
			thread.join();
		}
	}

	@Test
	@DisplayName("Get All Games")
	void getAllGames() {
		// Arrange
		Game game1 = new Game("Chess", LocalDate.now(), true);
		Game game2 = new Game("Checkers", LocalDate.now(), true);
		gameService.createGame(game1);
		gameService.createGame(game2);

		// Act
		List<Game> allGames = gameService.getAllGames();

		// Assert
		assertEquals(2, allGames.size());
		assertTrue(allGames.contains(game1));
		assertTrue(allGames.contains(game2));
	}

	@Test
	@DisplayName("Delete All Games")
	void deleteAllGames() {
		// Arrange
		Game game1 = new Game("Chess", LocalDate.now(), true);
		Game game2 = new Game("Checkers", LocalDate.now(), true);
		gameService.createGame(game1);
		gameService.createGame(game2);

		// Act
		int numDeleted = gameService.deleteAllGames();

		// Assert
		assertEquals(2, numDeleted);
		assertEquals(0, gameService.getAllGames().size());
	}
}