package com.particle.asset.manager.results;

import com.particle.asset.manager.DTO.AssetBodyDTO;
import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeBodyDTO;
import com.particle.asset.manager.DTO.MovementResponseBodyDTO;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
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
    public static class AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult
    {
        private StatusForControllerOperations status;
        private AssetTypeBusinessUnitAssetStatusTypeBodyDTO patchResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class AssetBodyDTOResult
    {
        private StatusForControllerOperations status;
        private AssetBodyDTO patchResponse;
    }

    @AllArgsConstructor
    @Getter
    public static class MovementBodyDTOResult
    {
        private StatusForControllerOperations status;
        private MovementResponseBodyDTO patchResponse;
    }
}
