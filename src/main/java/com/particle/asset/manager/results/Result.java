package com.particle.asset.manager.results;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.StatusForControllerOperations;
import com.particle.asset.manager.models.Asset;
import com.particle.asset.manager.models.AssetStatusType;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.models.BusinessUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Result
{
    @AllArgsConstructor
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
    }

    @AllArgsConstructor
    @Getter
    public static class AssetTypeDTOPutResult
    {
        private StatusForControllerOperations status;
        private AssetTypeRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class BusinessUnitRequestDtoPutResult
    {
        private StatusForControllerOperations status;
        private BusinessUnitRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetStatusTypeRequestDtoPutResult
    {
        private StatusForControllerOperations status;
        private AssetStatusTypeRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetBodyDTOResult
    {
        private StatusForControllerOperations status;
        private AssetRequestDto putResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class MovementBodyDTOResult
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
