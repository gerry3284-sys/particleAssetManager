package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class AssetResponses
{
    public static final Error BAD_REQUEST =
            new Error("400", "Empty Body or Null Value");
    public static final Error NOT_FOUND =
            new Error("404", "No Asset Record Was Found");
    public static final Error ALREADY_EXISTS =
            new Error("400", "Asset With That Serial Number Already Exists");
    public static final Error STATUS_ERROR =
            new Error("400", "Asset Status Cannot Be Changed Now");
    public static final Error CANNOT_UPDATE =
            new Error("409", "Asset Cannot Be Updated Because it's not AVAILABLE");
    public static final Error INVALID_STORAGE =
            new Error("400", "Invalid Storage Type, Data Size or Number is Invalid");
    public static final Error INVALID_RAM =
            new Error("400", "Invalid RAM Value");
}
