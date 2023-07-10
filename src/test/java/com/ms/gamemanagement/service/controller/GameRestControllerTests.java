package com.ms.gamemanagement.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ms.gamemanagement.controller.GameRestController;
import com.ms.gamemanagement.modal.Game;
import com.ms.gamemanagement.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GameRestController.class)
class GameRestControllerTests {

    @MockBean
    GameService gameService;

    @Autowired
    GameRestController gameController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

    private final String BASE_PATH = "/v1/games";

    @Test
    @DisplayName("Create Game - Success")
    void createGameSuccess() throws Exception {
        // Arrange
        String gameName = "Chess";
        LocalDate creationDate = LocalDate.now();
        boolean isActive = true;

        Game game = new Game(gameName, creationDate, isActive);
        when(gameService.createGame(game)).thenReturn(Optional.of(game));

        String requestContent = "{\"name\":\"" + gameName + "\",\"creationDate\":\"" + creationDate + "\",\"active\":" + isActive + "}";

        // Act
        ResultActions resultActions = mockMvc.perform(post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent));

        // Assert
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(gameName))
                .andExpect(jsonPath("$.creationDate[0]").value(creationDate.getYear()))
                .andExpect(jsonPath("$.creationDate[1]").value(creationDate.getMonthValue()))
                .andExpect(jsonPath("$.creationDate[2]").value(creationDate.getDayOfMonth()))
                .andExpect(jsonPath("$.active").value(isActive));


       verify(gameService, times(1)).createGame(game);
    }

    @Test
    @DisplayName("Get Game - Success")
    void getGameSuccess() throws Exception {
        // Arrange
        Game game = new Game("Chess", LocalDate.now(), true);
        when(gameService.getGame("Chess")).thenReturn(Optional.of(game));

        // Act
        ResultActions resultActions = mockMvc.perform(get(BASE_PATH + "/{name}", "Chess"));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chess"))
                .andExpect(jsonPath("$.creationDate[0]").value(game.getCreationDate().getYear()))
                .andExpect(jsonPath("$.creationDate[1]").value(game.getCreationDate().getMonthValue()))
                .andExpect(jsonPath("$.creationDate[2]").value(game.getCreationDate().getDayOfMonth()))
                .andExpect(jsonPath("$.active").value(true));
        verify(gameService, times(1)).getGame("Chess");
    }


    @Test
    @DisplayName("Get Game - Not Found")
    void getGameNotFound() throws Exception {
        // Arrange
        when(gameService.getGame("NonExistentGame")).thenReturn(Optional.empty());

        // Act
        ResultActions resultActions = mockMvc.perform(get(BASE_PATH + "/{name}", "NonExistentGame"));

        // Assert
        resultActions
                .andExpect(status().isNotFound());
        verify(gameService, times(1)).getGame("NonExistentGame");
    }

    @Test
    @DisplayName("Update Game - Success")
    void updateGameSuccess() throws Exception {
        // Arrange
        String gameName = "Chess";
        LocalDate creationDate = LocalDate.now();
        boolean isActive = true;

        Game game = new Game(gameName, creationDate, isActive);
        when(gameService.updateGame(eq("Chess"), eq(game))).thenReturn(Optional.of(game));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule

        String requestContent = objectMapper.writeValueAsString(game);

        // Act
        ResultActions resultActions = mockMvc.perform(put(BASE_PATH + "/{name}", "Chess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(requestContent));

        verify(gameService, times(1)).updateGame(eq("Chess"), eq(game));
    }


    @Test
    @DisplayName("Update Game - Not Found")
    void updateGameNotFound() throws Exception {
        // Arrange
        Game game = new Game("Chess", LocalDate.now(), true);
        when(gameService.updateGame("NonExistentGame", game)).thenReturn(Optional.empty());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestContent = objectMapper.writeValueAsString(game);

        // Act
        MvcResult mvcResult = mockMvc.perform(put(BASE_PATH + "/{name}", "NonExistentGame")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andReturn();

        // Assert
        int statusCode = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.NOT_FOUND.value(), statusCode);
        verify(gameService, times(1)).updateGame("NonExistentGame", game);
    }


    @Test
    @DisplayName("Delete Game - Success")
    void deleteGameSuccess() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(delete(BASE_PATH + "/{name}", "Chess"));

        // Assert
        resultActions
                .andExpect(status().isNoContent());
        verify(gameService, times(1)).deleteGame("Chess");
    }


    @Test
    @DisplayName("Get All Games - Success")
    void getAllGamesSuccess() throws Exception {
        // Arrange
        String dateString = "10-07-2023";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        Game game1 = new Game("Chess", localDate, true);
        Game game2 = new Game("Checkers", localDate, true);
        List<Game> games = Arrays.asList(game1, game2);
        when(gameService.getAllGames()).thenReturn(games);

        // Act
        ResultActions resultActions = mockMvc.perform(get(BASE_PATH));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chess"))
                .andExpect(jsonPath("$[0].creationDate[0]").value(2023))
                .andExpect(jsonPath("$[0].creationDate[1]").value(7))
                .andExpect(jsonPath("$[0].creationDate[2]").value(10))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].name").value("Checkers"))
                .andExpect(jsonPath("$[1].creationDate[0]").value(2023))
                .andExpect(jsonPath("$[1].creationDate[1]").value(7))
                .andExpect(jsonPath("$[1].creationDate[2]").value(10))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(gameService, times(1)).getAllGames();
    }


    @Test
    @DisplayName("Get All Games - Empty List")
    void getAllGamesEmpty() throws Exception {
        // Arrange
        when(gameService.getAllGames()).thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(get(BASE_PATH));

        // Assert
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        verify(gameService, times(1)).getAllGames();
    }

    @Test
    @DisplayName("Delete Game - Not Found")
    void deleteGameNotFound() throws Exception {
        // Arrange
        doNothing().when(gameService).deleteGame("NonExistentGame");
        //doThrow(new GameNotFoundException("Game not found")).when(gameService).deleteGame("NonExistentGame");

        // Act
        ResultActions resultActions = mockMvc.perform(delete(BASE_PATH + "/{name}", "NonExistentGame"));

        // Assert
        resultActions
                .andExpect(status().isNoContent());
        verify(gameService, times(1)).deleteGame("NonExistentGame");
    }

    @Test
    @DisplayName("Delete All Games - Success")
    void deleteAllGamesSuccess() throws Exception {
        // Arrange
        when(gameService.deleteAllGames()).thenReturn(2); // Assuming 2 games were deleted

        // Act
        ResultActions resultActions = mockMvc.perform(delete(BASE_PATH));

        // Assert
        resultActions
                .andExpect(status().isNoContent());

        verify(gameService, times(1)).deleteAllGames();
    }


}

