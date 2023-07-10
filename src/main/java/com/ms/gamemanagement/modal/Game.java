package com.ms.gamemanagement.modal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a game")
public class Game {

    @Schema(description = "Name of the game", example = "Chess")
    private String name;

    @Schema(description = "Creation date of the game", example = "2023-07-10")
    private LocalDate creationDate;

    @Schema(description = "Flag indicating if the game is active", example = "true")
    private boolean active;

}
