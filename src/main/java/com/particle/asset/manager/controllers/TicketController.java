package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.TicketRequestDto;
import com.particle.asset.manager.DTO.TicketResponseBodyDto;
import com.particle.asset.manager.enums.TicketOperations;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.TicketService;
import com.particle.asset.manager.swaggerResponses.GenericResponses;
import com.particle.asset.manager.swaggerResponses.TicketResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "ticket", description = "Ticket Manager")
public class TicketController
{
    private final TicketService service;

    public TicketController(TicketService service) { this.service = service; }

    // Stampa tutti i Ticket
    @GetMapping
    @Operation(summary = "Get all Tickets")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketResponseBodyDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.particle.asset.manager.models.Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<TicketResponseBodyDto>> getAllTypes() { return ResponseEntity.ok(service.getAllTickets()); }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific Ticket through its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketResponseBodyDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> getTicketById(@PathVariable Long id)
    {
        TicketResponseBodyDto searchedTicket = service.getTicketById(id);

        return searchedTicket != null ?ResponseEntity.ok(searchedTicket)
                :ResponseEntity.status(404).body(TicketResponses.USER_NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new Ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketResponseBodyDto.class))),
            @ApiResponse(responseCode = "400", description = "Business Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNPROCESSABLE_ENTITY))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> createTicket(@RequestBody TicketRequestDto ticket)
    {
        Result.TicketResult createdTicket = service.createTicket(ticket);

        if(createdTicket.getStatus().equals(TicketOperations.OK))
            return ResponseEntity.ok(createdTicket.getResponse());
        else if(createdTicket.getStatus().equals(TicketOperations.BAD_REQUEST))
            return ResponseEntity.status(400).body(TicketResponses.BAD_REQUEST);
        else if(createdTicket.getStatus().equals(TicketOperations.OPERATION_ERROR))
            return ResponseEntity.status(422).body(TicketResponses.OPERATION_ERROR);
        else if(createdTicket.getStatus().equals(TicketOperations.USER_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.USER_NOT_FOUND);
        else if(createdTicket.getStatus().equals(TicketOperations.ASSET_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.ASSET_NOT_FOUND);
        else // ASSET_TYPE_NOT_FOUND
            return ResponseEntity.status(404).body(TicketResponses.ASSET_TYPE_NOT_FOUND);
    }
}
