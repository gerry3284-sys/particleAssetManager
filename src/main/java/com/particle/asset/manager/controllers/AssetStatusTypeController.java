package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.AssetStatusTypeOperations;
import com.particle.asset.manager.enums.BusinessUnitOperations;
import com.particle.asset.manager.models.AssetStatusType;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.AssetStatusTypeService;
import com.particle.asset.manager.swaggerResponses.AssetStatusTypeResponses;
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
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<AssetStatusType>> getAllAssetStatusTypes() { return ResponseEntity.ok(service.getAllAssetStatusType()); }

    // Stampa un AssetStatusType tramite un dato id
    @GetMapping("/{code}")
    @Operation(summary = "Get a specific AssetStatusTypes through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusType.class))),
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
    public ResponseEntity<?> getAssetStatusTypeById(@PathVariable String code)
    {
        AssetStatusType searchedAssetStatusType = service.getAssetStatusTypeById(code);

        return searchedAssetStatusType != null ?ResponseEntity.ok(searchedAssetStatusType)
                :ResponseEntity.status(404).body(GenericResponses.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new AssetStatusType")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusTypeResponseDto.class))),
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
    public ResponseEntity<?> createAssetStatusType(
            @RequestBody AssetStatusTypeRequestDto assetStatusTypeDTO)
    {
        Result.AssetStatusTypeRequestDtoPutResult createdAssetStatusType =
                service.createAssetStatusType(assetStatusTypeDTO);

        if(createdAssetStatusType.getStatus() == AssetStatusTypeOperations.OK)
            return ResponseEntity.ok(createdAssetStatusType.getPutResponse());
        else if(createdAssetStatusType.getStatus() == AssetStatusTypeOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(AssetStatusTypeResponses.BAD_REQUEST);
        else
            return ResponseEntity.status(400).body(AssetStatusTypeResponses.ALREADY_EXISTS);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a specific AssetStatusType through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusTypeResponseDto.class))),
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
    public ResponseEntity<?> updateAssetStatusTypeById(@PathVariable String code, @RequestBody AssetStatusTypeRequestDto assetStatusTypeDTO)
    {
        Result.AssetStatusTypeRequestDtoPutResult updatedAssetStatusType = service.updateAssetStatusType(code, assetStatusTypeDTO);

        return switch(updatedAssetStatusType.getStatus())
        {
            case OK -> ResponseEntity.ok(updatedAssetStatusType.getPutResponse());
            case NOT_FOUND -> ResponseEntity.status(404).body(AssetStatusTypeResponses.NOT_FOUND);
            case BAD_REQUEST -> ResponseEntity.status(400).body(AssetStatusTypeResponses.BAD_REQUEST);
            case ALREADY_EXISTS -> ResponseEntity.status(400).body(AssetStatusTypeResponses.ALREADY_EXISTS);
        };
    }

    /*@PutMapping("/activateDeactivate/{code}")
    @Operation(summary = "Activate or Deactivate a specific AssetStatusType through its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetStatusTypeStatusResponseDto.class))),
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
    public ResponseEntity<?> activateDeactivateAssetStatusTypeById(@PathVariable String code)
    {
        AssetStatusTypeStatusResponseDto activatedDeactivatedAssetStatusType = service.activateDeactivateAssetStatusType(code);

        return activatedDeactivatedAssetStatusType != null ? ResponseEntity.ok(activatedDeactivatedAssetStatusType)
                : ResponseEntity.status(404).body(GenericResponses.NOT_FOUND);
    }*/
}
