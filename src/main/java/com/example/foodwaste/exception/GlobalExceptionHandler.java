package com.example.foodwaste.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ErrorBody(int status, String error, String message, Map<String, String> fieldErrors) { }

    private ResponseEntity<ErrorBody> body(HttpStatus s, String message, Map<String, String> fieldErrors) {
        return ResponseEntity.status(s).body(new ErrorBody(s.value(), s.getReasonPhrase(), message, fieldErrors));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorBody> api(ApiException ex) {
        return body(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Validation failed.", fields);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorBody> badInput(Exception ex) {
        return body(HttpStatus.BAD_REQUEST, "Invalid request. Check the date (yyyy-MM-dd) and number formats.", null);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorBody> database(DataAccessException ex) {
        log.error("Database error", ex);
        return body(HttpStatus.SERVICE_UNAVAILABLE, "Database error. Check that MySQL is running and the credentials are correct.", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> other(Exception ex) {
        if (ex instanceof ErrorResponse er) {   // Spring MVC errors such as 404 / 405
            int code = er.getStatusCode().value();
            return ResponseEntity.status(code).body(new ErrorBody(code, "Error", ex.getMessage(), null));
        }
        log.error("Unexpected error", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", null);
    }
}
