package org.example.cheapesttransferroute.ErrorHandlers;

import org.example.cheapesttransferroute.Service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {
    @Autowired
    TransferService transferService;
    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            logger.error(errorMessage);
        });
        transferService.clearData();
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Map<String, String> handleJsonFormatException(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid JSON format: " + ex.getMessage());
        logger.error(ex.getMessage());
        return error;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidJsonFormatException.class)
    @ResponseBody
    public Map<String, String> handleInvalidJsonFormatException(InvalidJsonFormatException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        logger.error(ex.getMessage());
        return error;
    }
}