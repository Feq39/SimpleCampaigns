package com.example.campains.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(
            ResourceNotFoundException exception,
            WebRequest request
    ) {
        return createProblemResponse(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                "RESOURCE_NOT_FOUND",
                publicMessage(exception, "Requested resource was not found."),
                request
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExists(
            ResourceAlreadyExistsException exception,
            WebRequest request
    ) {
        return createProblemResponse(
                HttpStatus.CONFLICT,
                "Resource already exists",
                "RESOURCE_ALREADY_EXISTS",
                publicMessage(exception, "The requested resource already exists."),
                request
        );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFunds(
            InsufficientFundsException exception,
            WebRequest request
    ) {
        return createProblemResponse(
                HttpStatus.CONFLICT,
                "Insufficient funds",
                "INSUFFICIENT_FUNDS",
                publicMessage(exception, "There are not enough funds to complete this operation."),
                request
        );
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            WebRequest request
    ) {
        logger.warn("Data integrity violation", exception);

        return createProblemResponse(
                HttpStatus.CONFLICT,
                "Data conflict",
                "DATA_INTEGRITY_CONFLICT",
                "The request conflicts with the current data state.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpectedException(
            Exception exception,
            WebRequest request
    ) {
        logger.error("Unhandled exception", exception);

        return createProblemResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                "INTERNAL_ERROR",
                "An unexpected server error occurred.",
                request
        );
    }


    private ResponseEntity<Object> createProblemResponse(
            HttpStatus status,
            String title,
            String code,
            String detail,
            WebRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("code", code);
        return createResponseEntity(
                problemDetail,
                new HttpHeaders(),
                status,
                request
        );
    }

    private static String publicMessage(
            Exception exception,
            String fallback
    ) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? fallback : message;
    }

}
