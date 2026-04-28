package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.BusinessUnitRequestDto;
import com.particle.asset.manager.DTO.BusinessUnitResponseDto;
import com.particle.asset.manager.DTO.BusinessUnitStatusResponseDto;
import com.particle.asset.manager.enums.BusinessUnitOperations;
import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.BusinessUnitService;
import com.particle.asset.manager.swaggerResponses.BusinessUnitResponses;
import com.particle.asset.manager.swaggerResponses.GenericResponses;
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
@RequestMapping("/businessUnit")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "businessUnit", description = "Business Unit for Assets and Users Manager")
public class BusinessUnitController
{
    private final BusinessUnitService service;

    public BusinessUnitController(BusinessUnitService service) { this.service = service; }

    // Stampa di tutte le businessUnit
    @GetMapping
    @Operation(summary = "Get all BusinessUnits")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnitResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<BusinessUnitResponseDto>> getAllBusinessUnits() { return ResponseEntity.ok(service.getAllBusinessUnits()); }

    // Stampa una businessUnit tramite un dato ID
    @GetMapping("/{code}")
    @Operation(summary = "Get a specific BusinessUnits through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnitResponseDto.class))),
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
    public ResponseEntity<?> getBusinessUnitById(@PathVariable String code)
    {
        BusinessUnitResponseDto searchedBusinessUnit = service.getBusinessUnitById(code);

        return searchedBusinessUnit != null ?ResponseEntity.ok(searchedBusinessUnit)
                :ResponseEntity.status(404).body(BusinessUnitResponses.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new businessUnit")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Business Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> createAssetBusinessUnit(
            @RequestBody BusinessUnitRequestDto businessUnitDTO)
    {
        Result.BusinessUnitRequestDtoPutResult createdBusinessUnit = service.createBusinessUnit(businessUnitDTO);

        if(createdBusinessUnit.getStatus() == BusinessUnitOperations.OK)
            return ResponseEntity.ok(createdBusinessUnit.getPutResponse());
        else if(createdBusinessUnit.getStatus() == BusinessUnitOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(BusinessUnitResponses.BAD_REQUEST);
        else
            return ResponseEntity.status(400).body(BusinessUnitResponses.ALREADY_EXISTS);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a specific BusinessUnit through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnitResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Business Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.BAD_REQUEST_EXAMPLE))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> updateBusinessUnitById(@PathVariable String code,
                                    @RequestBody BusinessUnitRequestDto businessUnitDTO)
    {
        Result.BusinessUnitRequestDtoPutResult updatedBusinessUnit =
                service.updateBusinessUnitById(code, businessUnitDTO);

        if(updatedBusinessUnit.getStatus() == BusinessUnitOperations.OK)
            return ResponseEntity.ok(updatedBusinessUnit.getPutResponse());
        else if(updatedBusinessUnit.getStatus() == BusinessUnitOperations.NOT_FOUND)
            return ResponseEntity.status(404).body(BusinessUnitResponses.NOT_FOUND);
        else if(updatedBusinessUnit.getStatus() == BusinessUnitOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(BusinessUnitResponses.BAD_REQUEST);
        else // ALREADY_EXISTS
            return ResponseEntity.status(400).body(BusinessUnitResponses.ALREADY_EXISTS);
    }

    @PutMapping("/activateDeactivate/{code}")
    @Operation(summary = "Activate or Deactivate a specific BusinessUnit through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnitStatusResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.FORBIDDEN_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "423", description = "Table State is Blocked",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.TABLE_STATE_BLOCKS_OPERATION_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> activateDeactivateBusinessUnitById(@PathVariable String code)
    {
        Result.BusinessUnitActiveDtoPutResult activatedDeactivatedBusinessUnit = service.activateDeactivateBusinessUnitById(code);

        if(activatedDeactivatedBusinessUnit.getStatus() == BusinessUnitOperations.OK)
            return ResponseEntity.ok(activatedDeactivatedBusinessUnit.getPutResponse());
        else if(activatedDeactivatedBusinessUnit.getStatus() == BusinessUnitOperations.NOT_FOUND)
            return ResponseEntity.status(404).body(BusinessUnitResponses.NOT_FOUND);
        else // CANNOT_DEACTIVATE
            return ResponseEntity.status(423).body(BusinessUnitResponses.CANNOT_DEACTIVATE);
    }
}
