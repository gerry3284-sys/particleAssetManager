package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.TicketReplyRequestDto;
import com.particle.asset.manager.DTO.TicketReplyResponseDto;
import com.particle.asset.manager.DTO.TicketRequestDto;
import com.particle.asset.manager.DTO.FetchTicketResponseBodyDto;
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
@Tag(name = "ticketReplies", description = "Ticket Manager")
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
                            schema = @Schema(implementation = FetchTicketResponseBodyDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = com.particle.asset.manager.models.Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<FetchTicketResponseBodyDto>> getAllTypes() { return ResponseEntity.ok(service.getAllTickets()); }

    @GetMapping("/{code}")
    @Operation(summary = "Get a specific Ticket through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FetchTicketResponseBodyDto.class))),
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
    public ResponseEntity<?> getTicketById(@PathVariable String code)
    {
        FetchTicketResponseBodyDto searchedTicket = service.getTicketByCode(code);

        return searchedTicket != null ?ResponseEntity.ok(searchedTicket)
                :ResponseEntity.status(404).body(TicketResponses.TICKET_NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new Ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FetchTicketResponseBodyDto.class))),
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
        else if(createdTicket.getStatus().equals(TicketOperations.ASSET_TYPE_NOT_FOUND))
            return ResponseEntity.status(400).body(TicketResponses.ASSET_TYPE_NOT_FOUND);
        else if(createdTicket.getStatus().equals(TicketOperations.OPERATION_ERROR))
            return ResponseEntity.status(422).body(TicketResponses.OPERATION_ERROR);
        else if(createdTicket.getStatus().equals(TicketOperations.USER_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.USER_NOT_FOUND);
        else if(createdTicket.getStatus().equals(TicketOperations.INVALID_USER_TYPE))
            return ResponseEntity.status(400).body(TicketResponses.INVALID_USER_TYPE);
        else if(createdTicket.getStatus().equals(TicketOperations.ASSET_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.ASSET_NOT_FOUND);
        else // BAD_REQUEST
            return ResponseEntity.status(404).body(TicketResponses.BAD_REQUEST);
    }

    @GetMapping("/{ticketCode}/replies")
    @Operation(summary = "Get the whole ticket chat")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketReplyResponseDto.class))),
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
    public ResponseEntity<?> getAllReplies(@PathVariable String ticketCode)
    {
        List<TicketReplyResponseDto> replies = service.getAllTicketReplies(ticketCode);

        return replies != null ?ResponseEntity.ok(replies)
                :ResponseEntity.status(404).body(TicketResponses.TICKET_NOT_FOUND);
    }

    @PostMapping("/{ticketCode}/reply")
    @Operation(summary = "Reply to a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FetchTicketResponseBodyDto.class))),
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
    public ResponseEntity<?> replyToAMessage(@PathVariable String ticketCode,
                                             @RequestBody TicketReplyRequestDto reply)
    {
        Result.TicketReplyResult replied = service.reply(ticketCode, reply);

        if(replied.getStatus().equals(TicketOperations.OK))
            return ResponseEntity.ok(replied.getResponse());
        else if(replied.getStatus().equals(TicketOperations.TICKET_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.TICKET_NOT_FOUND);
        else if(replied.getStatus().equals(TicketOperations.CANNOT_REPLY))
            return ResponseEntity.status(423).body(TicketResponses.CANNOT_REPLY);
        else if(replied.getStatus().equals(TicketOperations.ALREADY_REPLIED))
            return ResponseEntity.status(409).body(TicketResponses.ALREADY_REPLIED); // Conflict
        else if(replied.getStatus().equals(TicketOperations.USER_NOT_FOUND))
            return ResponseEntity.status(404).body(TicketResponses.USER_NOT_FOUND);
        else if(replied.getStatus().equals(TicketOperations.CANNOT_CLOSE))
            return ResponseEntity.status(403).body(TicketResponses.CANNOT_CLOSE);
        else // BAD_REQUEST
            return ResponseEntity.status(400).body(TicketResponses.BAD_REQUEST);
    }
}
