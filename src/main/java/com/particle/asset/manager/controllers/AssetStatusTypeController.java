package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO;
import com.particle.asset.manager.models.AssetStatusType;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.AssetStatusTypeService;
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
@RequestMapping("/assetStatusType")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "AssetStatusType", description = "Type of Status for the Asset")
public class AssetStatusTypeController
{
    private final AssetStatusTypeService service;

    public AssetStatusTypeController(AssetStatusTypeService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Get all AssetStatusTypes")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<AssetStatusType>> getAllAssetStatusTypes() { return ResponseEntity.ok(service.getAllAssetStatusType()); }

    // Stampa un AssetStatusType tramite un dato id
    @GetMapping("/{id}")
    @Operation(summary = "Get a specific AssetStatusTypes through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
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
    public ResponseEntity<?> getAssetStatusTypeById(@PathVariable Long id)
    {
        AssetStatusType searchedAssetStatusType = service.getAssetStatusTypeById(id);

        return searchedAssetStatusType != null ?ResponseEntity.ok(searchedAssetStatusType)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new AssetStatusType")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
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
    public ResponseEntity<?> createAssetStatusType(
            @RequestBody AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO assetStatusTypeDTO)
    {
        AssetStatusType createdAssetStatusType = service.createAssetStatusType(assetStatusTypeDTO);

        return createdAssetStatusType != null ?ResponseEntity.ok(createdAssetStatusType)
                :ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a specific AssetStatusType through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
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
    public ResponseEntity<?> updateAssetStatusTypeById(@PathVariable Long id,
                                   @RequestBody AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO assetStatusTypeDTO)
    {
        Result.AssetStatusTypeResult updatedAssetStatusType = service.updateAssetStatusType(id, assetStatusTypeDTO);

        return switch(updatedAssetStatusType.getStatus())
        {
            case OK -> ResponseEntity.ok(updatedAssetStatusType.getAssetStatusType());
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
            case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
        };
    }

    @PutMapping("/activateDeactivate/{id}")
    @Operation(summary = "Activate or Deactivate a specific AssetStatusType through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
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
    public ResponseEntity<?> activateDeactivateAssetStatusTypeById(@PathVariable Long id)
    {
        AssetStatusType activatedDeactivatedAssetStatusType = service.activateDeactivateAssetStatusType(id);

        return activatedDeactivatedAssetStatusType != null ? ResponseEntity.ok(activatedDeactivatedAssetStatusType)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }
}
