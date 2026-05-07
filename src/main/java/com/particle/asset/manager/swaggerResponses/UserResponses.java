package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class UserResponses
{
    public static final Error BAD_REQUEST =
            new Error("400", "Empty Field or Null Value");
    public static final Error USER_NOT_FOUND =
            new Error("404", "No User Record Was Found");
}
