package com.particle.asset.manager.swaggerResponses;

import com.particle.asset.manager.models.Error;

public class SwaggerResponses
{
    // ↓ Utilizzati per mostrare gli errori JSON
    public static final Error BAD_REQUEST = new Error("400", "Not Valid Input");
    // ↓ Gestiti da SecurityConfig.java
    //public static final Error UNAUTHORIZED_ACCESS = new Error("401", "Access Token is missing or invalid");
    //public static final Error FORBIDDEN_ACCESS = new Error("403", "Access is Forbidden");
    public static final Error NOT_FOUND = new Error("404", "No Record was found");
    public static final Error INTERNAL_SERVER_ERROR = new Error("500", "Internal Server Error");

    // ↓ Utilizzati come esempio Swagger
    public static final String BAD_REQUEST_EXAMPLE =
            """
            {
                "errorCode": "400",
                "errorDescription": "Not Valid Input"
            }
            """;

    public static final String UNAUTHORIZED_ACCESS_EXAMPLE =
            """
            {
                "errorCode": "401",
                "errorDescription": "Access Token is missing or invalid"
            }
            """;

    public static final String FORBIDDEN_ACCESS_EXAMPLE =
            """
            {
                "errorCode": "403",
                "errorDescription": "Access is Forbidden"
            }
            """;

    public static final String NOT_FOUND_EXAMPLE =
            """
            {
                "errorCode": "404",
                "errorDescription": "No Record was found"
            }
            """;

    public static final String INTERNAL_SERVER_ERROR_EXAMPLE =
            """
            {
                "errorCode": "500",
                "errorDescription": "Internal Server Error"
            }
            """;
}
