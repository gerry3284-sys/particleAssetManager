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
}
