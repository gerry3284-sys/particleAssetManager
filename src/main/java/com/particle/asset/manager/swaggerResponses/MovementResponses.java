package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class MovementResponses
{
    public static final Error BAD_REQUEST =
            new Error("400", "Empty Body or Null Value");
    public static final Error INVALID_MOVEMENT_TYPE =
            new Error("400", "Movement Type Is Not Valid");
    public static final Error ASSET_NOT_FOUND =
            new Error("404", "No Asset Record Was Found");
    public static final Error USER_NOT_FOUND =
            new Error("404", "No User Record Was Found");
    public static final Error ASSET_MOVEMENT_NOT_FOUND =
            new Error("404", "No Asset Movement Record Was Found");
    public static final Error USER_MOVEMENT_NOT_FOUND =
            new Error("404", "No User Movement Record Was Found");
    public static final Error ASSET_STATE_BLOCKS_OPERATION =
            new Error("423", "Asset State Prevents This Operation");
    public static final Error INVALID_FILE_NAME =
            new Error("400", "File Name Is Invalid");
}