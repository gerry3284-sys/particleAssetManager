package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class TicketResponses
{
    public static final Error BAD_REQUEST =
            new Error("400", "Empty Body or Null Value");
    // 422 → Unprocessable Entity
    public static final Error OPERATION_ERROR =
            new Error("422", "Wrong Data for that Operation");
    public static final Error USER_NOT_FOUND =
            new Error("404", "User Not Found");
    public static final Error ASSET_NOT_FOUND =
            new Error("404", "Asset Not Found");
    public static final Error ASSET_TYPE_NOT_FOUND =
            new Error("404", "Asset Type Not Found");
}
