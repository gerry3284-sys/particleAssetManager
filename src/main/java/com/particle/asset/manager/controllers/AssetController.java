package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.AssetOperations;
import com.particle.asset.manager.enums.MovementOperations;
import com.particle.asset.manager.models.Asset;
import com.particle.asset.manager.models.Error;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.services.AssetService;
import com.particle.asset.manager.swaggerResponses.AssetResponses;
import com.particle.asset.manager.swaggerResponses.GenericResponses;
import com.particle.asset.manager.swaggerResponses.MovementResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@RequestMapping("/asset")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "assetMovementManager", description = "Assets Manager and Movement Manager")
public class AssetController
{
    private final AssetService service;

    public AssetController(AssetService service) { this.service = service; }

    // Stampa di tutti gli asset
    @GetMapping
    @Operation(summary = "Get all Assets")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Asset.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<Asset>> getAllAssets() { return ResponseEntity.ok(service.getAllAssets()); }

    // Stampa i valori di un asset dato il suo id
    @GetMapping("/{code}")
    @Operation(summary = "Get a specific Asset through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Asset.class))),
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
    public ResponseEntity<?> getAssetById(@PathVariable String code)
    {
        Asset assetById = service.getAssetById(code);

        return assetById != null ?ResponseEntity.ok(assetById)
                :ResponseEntity.status(404).body(AssetResponses.NOT_FOUND);
    }

    // Creazione Asset
    @PostMapping
    @Operation(summary = "Create a new Asset")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetResponseDto.class))),
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
    public ResponseEntity<?> createAsset(@RequestBody AssetRequestDto assetDTO)
    {
        Result.AssetDtoResult createdAsset = service.createAsset(assetDTO);


        if(createdAsset.getStatus() == AssetOperations.OK)
            return ResponseEntity.ok(createdAsset.getPutResponse());
        else if(createdAsset.getStatus() == AssetOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(AssetResponses.BAD_REQUEST);
        else
            return ResponseEntity.status(400).body(AssetResponses.ALREADY_EXISTS);
    }

    // Aggiorna i valori di un asset dato il suo id
    @PutMapping("/{code}")
    @Operation(summary = "Update a specific Asset through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetResponseDto.class))),
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
    public ResponseEntity<?> updateAssetByCode(@PathVariable String code, @RequestBody AssetRequestDto assetDTO)
    {
        Result.AssetDtoResult updatedAsset = service.updateAssetByCode(code, assetDTO);

        if(updatedAsset.getStatus() == AssetOperations.OK)
            return ResponseEntity.ok(updatedAsset.getPutResponse());
        else if(updatedAsset.getStatus() == AssetOperations.NOT_FOUND)
            return ResponseEntity.status(404).body(AssetResponses.NOT_FOUND);
        else if(updatedAsset.getStatus() == AssetOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(AssetResponses.BAD_REQUEST);
        else
            return ResponseEntity.status(400).body(AssetResponses.ALREADY_EXISTS);
    }

    @PutMapping("/updateAssetStatus/{code}")
    @Operation(summary = "Update the Status of a specific Asset through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetResponseDto.class))),
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
    public ResponseEntity<?> updateAssetStatusByCode(@PathVariable String code,
                                                     @RequestBody AssetStatusUpdateRequestDto statusCode)
    {
        Result.AssetDtoResult updatedStatus = service.updateStatusByCode(code, statusCode);

        if(updatedStatus.getStatus() == AssetOperations.OK)
            return ResponseEntity.ok(updatedStatus.getPutResponse());
        else if(updatedStatus.getStatus() == AssetOperations.NOT_FOUND)
            return ResponseEntity.status(404).body(AssetResponses.NOT_FOUND);
        else if(updatedStatus.getStatus() == AssetOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(AssetResponses.BAD_REQUEST);
        else
            return  ResponseEntity.status(400).body(AssetResponses.STATUS_ERROR);
    }

    // Stampa i movimenti di un asset dato il suo code
    @GetMapping("/{code}/movement")
    @Operation(summary = "Get all the Movements for a specific Asset through its Code")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementSummaryResponseDto.class))),
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
    public ResponseEntity<?> getAssetMovements(@PathVariable String code)
    {
        List<MovementSummaryResponseDto> movements = service.getAssetMovementDTO(code);

        return movements != null ?ResponseEntity.ok(movements)
                :ResponseEntity.status(404).body(MovementResponses.ASSET_MOVEMENT_NOT_FOUND);
    }

    @GetMapping("/list")
    @Operation(summary = "Get the Asset List")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssetListRowResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Not Authorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.UNAUTHORIZED_ACCESS_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<List<AssetListRowResponseDto>> getAllAssetList()
    {
        return ResponseEntity.ok(service.getAssetList());
    }

    // Inserisci un movimento di un asset dato il suo code
    @PostMapping("{code}/movement")
    @Operation(summary = "Assign/Return/Dismiss a specific Asset through its code")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseBodyDto.class))),
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
    public ResponseEntity<?> assignReturnedDismissAsset(@PathVariable String code,
                                                        @RequestBody MovementRequestBodyDto movementDTO)
    {
        Result.MovementDtoResult movementOperation = service.assignReturnedDismissAsset(code, movementDTO);

        if(movementOperation.getStatus() == MovementOperations.OK)
            return ResponseEntity.ok(movementOperation.getPutResponse());
        else if(movementOperation.getStatus() == MovementOperations.BAD_REQUEST)
            return ResponseEntity.status(400).body(MovementResponses.BAD_REQUEST);
        else if(movementOperation.getStatus() == MovementOperations.INVALID_MOVEMENT_TYPE)
            return ResponseEntity.status(400).body(MovementResponses.INVALID_MOVEMENT_TYPE);
        else if(movementOperation.getStatus() == MovementOperations.ASSET_NOT_FOUND)
            return ResponseEntity.status(404).body(MovementResponses.ASSET_NOT_FOUND);
        else if(movementOperation.getStatus() == MovementOperations.USER_NOT_FOUND)
            return ResponseEntity.status(404).body(MovementResponses.USER_NOT_FOUND);
        else // ASSET_STATE_BLOCKS_OPERATION
            return ResponseEntity.status(423).body(MovementResponses.ASSET_STATE_BLOCKS_OPERATION);
    }

    // Ottieni il PDF attraverso il codice dell'asset e del movimento
    @GetMapping(value = "/{assetCode}/movement/{movementCode}/receipt",
                produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Download the receipt PDF for a specific Movement through the asset and movement code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF Receipt",
                            content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.NOT_FOUND_EXAMPLE))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = GenericResponses.INTERNAL_SERVER_ERROR_EXAMPLE)))})
    public ResponseEntity<?> getMovementReceipt(@PathVariable String assetCode,
                                                @PathVariable String movementCode)
    {
        Result.ReceiptResult receipt = service.getMovementReceipt(assetCode, movementCode);
        return switch (receipt.getStatus())
        {
            case OK -> ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + receipt.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(receipt.getPdfBytes()); // Ottiene il PDF
            case INVALID_FILE_NAME -> ResponseEntity.status(400)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MovementResponses.INVALID_FILE_NAME);
            case FILE_IS_MISSING -> ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MovementResponses.FILE_IS_MISSING);
            case ASSET_NOT_FOUND -> ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MovementResponses.ASSET_NOT_FOUND);
            case MOVEMENT_NOT_FOUND -> ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MovementResponses.MOVEMENT_NOT_FOUND);
            case DIFFERENT_ASSET_CODE -> ResponseEntity.status(400)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MovementResponses.DIFFERENT_ASSET_CODE);
            default -> ResponseEntity.status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(GenericResponses.INTERNAL_SERVER_ERROR);
        };
    }
}
