package com.particle.asset.manager.controllers;

import com.particle.asset.manager.swaggerResponses.SwaggerResponses;
import com.particle.asset.manager.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handlerInternalServerError(Exception e)
    {
        e.printStackTrace(); // Log dell'errore per debugging

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SwaggerResponses.INTERNAL_SERVER_ERROR);
    }
}
