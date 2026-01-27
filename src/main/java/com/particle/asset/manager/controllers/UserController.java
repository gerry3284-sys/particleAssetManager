package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.MovementSummaryDTO;
import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.services.UserService;
import com.particle.asset.manager.swaggerResponses.SwaggerResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "user", description = "User and Movement Manager")
public class UserController
{
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    // Stampa di tutti gli user
    @GetMapping
    @Operation(summary = "Get all Users")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<User>> getAllUsers() { return ResponseEntity.ok(service.getAllUsers()); }

    // Stampa i valori di uno user dato il suo id
    @GetMapping("/{id}")
    @Operation(summary = "Get a specific User through their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> getUserById(@PathVariable Long id)
    {
        User userById = service.getUserById(id);

        return userById != null ?ResponseEntity.ok(userById)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }

    // Stampa tutti i movimenti dello user dato il suo id
    @GetMapping("/{id}/movement")
    @Operation(summary = "Get all the Movements for a specific User through their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementSummaryDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> getUserMovements(@PathVariable Long id)
    {
        List<MovementSummaryDTO> movements = service.getUserMovements(id);

        return movements != null ?ResponseEntity.ok(movements)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }
}
