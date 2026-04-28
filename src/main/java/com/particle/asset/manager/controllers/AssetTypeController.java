package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.AssetTypeRequestDto;
import com.particle.asset.manager.DTO.AssetTypeResponseDto;
import com.particle.asset.manager.DTO.AssetTypeStatusResponseDto;
import com.particle.asset.manager.enums.AssetTypeOperations;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.swaggerResponses.AssetTypeResponses;
import com.particle.asset.manager.swaggerResponses.GenericResponses;
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
                                schema = @Schema(implementation = AssetTypeResponseDto.class))),
                @ApiResponse(responseCode = "401", description = "Not Authorized",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Error.class),
                                examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Error.class),
                                examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<AssetTypeResponseDto>> getAllTypes() { return ResponseEntity.ok(service.getAllTypes()); }

    // Stampa un AssetType tramite un dato id
    @GetMapping("/{code}")
    @Operation(summary = "Get a specific AssetType through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeResponseDto.class))),
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
    public ResponseEntity<?> getTypeByCode(@PathVariable String code)
    {
        AssetTypeResponseDto searchedAssetType = service.getAssetTypeByCode(code);

        return searchedAssetType != null ?ResponseEntity.ok(searchedAssetType)
                :ResponseEntity.status(404).body(AssetTypeResponses.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new Type")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeResponseDto.class))),
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
    public ResponseEntity<?> createAssetType(@RequestBody AssetTypeRequestDto assetTypeDTO)
    {
        Result.AssetTypeDTOPutResult createdAssetType = service.createType(assetTypeDTO);

        if(createdAssetType.getStatus() == AssetTypeOperations.OK)
            return ResponseEntity.ok(createdAssetType.getPutResponse());
        else if(createdAssetType.getStatus() == AssetTypeOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(AssetTypeResponses.BAD_REQUEST);
        else // ALREADY_EXISTS
            return ResponseEntity.status(400).body(AssetTypeResponses.ALREADY_EXISTS);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update a specific Type through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeResponseDto.class))),
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
    public ResponseEntity<?> updateTypeById(@PathVariable String code, @RequestBody AssetTypeRequestDto assetTypeDTO)
    {
        Result.AssetTypeDTOPutResult updatedAssetType = service.updateTypeByCode(code, assetTypeDTO);

        return switch(updatedAssetType.getStatus())
        {
            case OK -> ResponseEntity.ok(updatedAssetType.getPutResponse());
            case NOT_FOUND -> ResponseEntity.status(404).body(AssetTypeResponses.NOT_FOUND);
            case BAD_REQUEST -> ResponseEntity.status(400).body(AssetTypeResponses.BAD_REQUEST);
            case ALREADY_EXISTS -> ResponseEntity.status(400).body(AssetTypeResponses.ALREADY_EXISTS);
        };
    }

    @PutMapping("/activateDeactivate/{code}")
    @Operation(summary = "Activate or Deactivate a specific Type through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetTypeStatusResponseDto.class))),
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
    public ResponseEntity<?> activateDeactivateTypeById(@PathVariable String code)
    {
        AssetTypeStatusResponseDto activatedDeactivatedAssetType =
                service.activateDeactivateTypeById(code);

        return activatedDeactivatedAssetType != null ?ResponseEntity.ok(activatedDeactivatedAssetType)
                :ResponseEntity.status(404).body(GenericResponses.NOT_FOUND);
    }
}
