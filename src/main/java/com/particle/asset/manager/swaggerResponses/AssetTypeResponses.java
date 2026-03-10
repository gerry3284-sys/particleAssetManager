package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class AssetTypeResponses
{
    public static final Error BAD_REQUEST = new Error("400", "Empty Body or Null Value");
    public static final Error NOT_FOUND = new Error("404", "No Asset Type Record Was Found");
    public static final Error ALREADY_EXISTS = new Error("400", "Asset Type Already Exists");
}
