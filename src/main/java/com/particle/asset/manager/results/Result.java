package com.particle.asset.manager.results;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Result
{
    /*@AllArgsConstructor
    @Getter
    public static class AssetTypeResult
    {
        private StatusForControllerOperations status;
        private AssetType assetType;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetStatusTypeResult
    {
        private StatusForControllerOperations status;
        private AssetStatusType assetStatusType;
    }

    @AllArgsConstructor
    @Getter
    public static class BusinessUnitResult
    {
        private StatusForControllerOperations status;
        private BusinessUnit businessUnit;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetResult
    {
        private StatusForControllerOperations status;
        private Asset asset;
    }*/

    @AllArgsConstructor
    @Getter
    public static class AssetTypeDTOPutResult
    {
        private AssetTypeOperations status;
        private AssetTypeRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class BusinessUnitRequestDtoPutResult
    {
        private BusinessUnitOperations status;
        private BusinessUnitRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetStatusTypeRequestDtoPutResult
    {
        private AssetStatusTypeOperations status;
        private AssetStatusTypeRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetDtoResult
    {
        private AssetOperations status;
        private AssetResponseDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class MovementDtoResult
    {
        private StatusForControllerOperations status;
        private MovementResponseBodyDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class ReceiptResult
    {
        private StatusForControllerOperations status;
        private byte[] pdfBytes;
        private String fileName;
    }
}
