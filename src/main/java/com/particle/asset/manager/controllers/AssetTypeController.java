package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO;
import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeBodyDTO;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.swaggerResponses.SwaggerResponses;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.services.AssetTypeService;
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
@RequestMapping("/assetType")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "assetType", description = "Type of Asset Manager")
public class AssetTypeController
{
   private final AssetTypeService service;

   public AssetTypeController(AssetTypeService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Get all assetTypes")
    @ApiResponses({
                @ApiResponse(responseCode = "200",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = AssetType.class))),
                @ApiResponse(responseCode = "401", description = "Not Authorized",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Error.class),
                                examples = @ExampleObject(value = SwaggerResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Error.class),
                                examples = @ExampleObject(value = SwaggerResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<AssetType>> getAllTypes() { return ResponseEntity.ok(service.getAllTypes()); }

    // Stampa un AssetType tramite un dato id
    @GetMapping("/{code}")
    @Operation(summary = "Get a specific AssetType through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetType.class))),
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
    public ResponseEntity<?> getTypeByID(@PathVariable String code)
    {
        AssetType searchedAssetType = service.getAssetTypeById(code);

        return searchedAssetType != null ?ResponseEntity.ok(searchedAssetType)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new Type")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeBusinessUnitAssetStatusTypeBodyDTO.class))),
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
    public ResponseEntity<?> createAssetType(@RequestBody AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetTypeDTO)
    {
        AssetTypeBusinessUnitAssetStatusTypeBodyDTO createdAssetType = service.createType(assetTypeDTO);

        return createdAssetType != null ?ResponseEntity.ok(createdAssetType)
                :ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a specific Type through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeBusinessUnitAssetStatusTypeBodyDTO.class))),
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
    public ResponseEntity<?> updateTypeById(@PathVariable String code,
                                            @RequestBody AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetTypeDTO)
    {
        Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult updatedAssetType = service.updateTypeById(code, assetTypeDTO);

        return switch(updatedAssetType.getStatus())
        {
            case OK -> ResponseEntity.ok(updatedAssetType.getPatchResponse());
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
            case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SwaggerResponses.BAD_REQUEST);
        };
    }

    @PutMapping("/activateDeactivate/{code}")
    @Operation(summary = "Activate or Deactivate a specific Type through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO.class))),
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
    public ResponseEntity<?> activateDeactivateTypeById(@PathVariable String code)
    {
        AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO activatedDeactivatedAssetType =
                service.activateDeactivateTypeById(code);

        return activatedDeactivatedAssetType != null ?ResponseEntity.ok(activatedDeactivatedAssetType)
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(SwaggerResponses.NOT_FOUND);
    }
}
