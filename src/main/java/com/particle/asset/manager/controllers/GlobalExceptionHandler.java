package com.particle.asset.manager.controllers;

import com.particle.asset.manager.swaggerResponses.GenericResponses;
import com.particle.asset.manager.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleInvalidJson(HttpMessageNotReadableException ex)
    {
        //ex.printStackTrace(); // Log dell'errore per debugging

        return ResponseEntity.status(400).body(new Error("400", "Invalid or unrecognized value in request body"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handlerInternalServerError(Exception e)
    {
        e.printStackTrace(); // Log dell'errore per debugging

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(GenericResponses.INTERNAL_SERVER_ERROR);
    }
}
