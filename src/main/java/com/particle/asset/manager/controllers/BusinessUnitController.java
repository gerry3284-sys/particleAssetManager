package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO;
import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.BusinessUnitService;
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
                            schema = @Schema(implementation = BusinessUnit.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<BusinessUnit>> getAllBusinessUnits() { return ResponseEntity.ok(service.getAllBusinessUnits()); }

    // Stampa una businessUnit tramite un dato ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a specific BusinessUnits through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnit.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> getBusinessUnitById(@PathVariable Long id)
    {
        BusinessUnit searchedBusinessUnit = service.getBusinessUnitById(id);

        return searchedBusinessUnit != null ?ResponseEntity.ok(searchedBusinessUnit)
                :ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
    }

    @PostMapping
    @Operation(summary = "Create a new businessUnit")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnit.class))),
            @ApiResponse(responseCode = "400", description = "Business Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.BAD_REQUEST_EXAMPLE))),
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
    public ResponseEntity<?> createAssetBusinessUnit(
            @RequestBody AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO businessUnitDTO)
    {
        BusinessUnit createdBusinessUnit = service.createBusinessUnit(businessUnitDTO);

        return createdBusinessUnit != null ?ResponseEntity.ok(createdBusinessUnit)
                :ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a specific BusinessUnit through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnit.class))),
            @ApiResponse(responseCode = "400", description = "Business Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.BAD_REQUEST_EXAMPLE))),
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
    public ResponseEntity<?> updateBusinessUnitById(@PathVariable Long id,
                                    @RequestBody AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO businessUnitDTO)
    {
        Result.BusinessUnitResult updatedBusinessUnit = service.updateBusinessUnitById(id, businessUnitDTO);

        return switch(updatedBusinessUnit.getStatus())
        {
            case OK -> ResponseEntity.ok(updatedBusinessUnit.getBusinessUnit());
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
            case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
        };
    }

    @PutMapping("/activateDeactivate/{id}")
    @Operation(summary = "Activate or Deactivate a specific BusinessUnit through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BusinessUnit.class))),
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
    public ResponseEntity<?> activateDeactivateBusinessUnitById(@PathVariable Long id)
    {
        BusinessUnit activatedDeactivatedBusinessUnit = service.activateDeactivateBusinessUnitById(id);

        return activatedDeactivatedBusinessUnit != null ?ResponseEntity.ok(activatedDeactivatedBusinessUnit)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }
}
