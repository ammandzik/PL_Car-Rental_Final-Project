package pl.coderslab.carrental.advice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.coderslab.carrental.exception.*;
import pl.coderslab.carrental.response.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Invalid values");

        var wrapped = new Exception(errors);
        return createResponse(HttpStatus.BAD_REQUEST, wrapped);
    }


    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(IllegalStateException exception) {

        log.error("Handling illegal state exception exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {

        log.error("Handling illegal argument exception exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {

        log.error("Handling entity not found exception: {}", exception.getMessage());
        return createResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        log.error("Handling method argument type mismatch exception: {}", exception.getMessage());

        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(CarAlreadyRentedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCarAlreadyRented(CarAlreadyRentedException exception) {

        log.error("Handling car already rented exception: {}", exception.getMessage());

        return createResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(ReviewNotAllowedYetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleReviewNotAllowedException(ReviewNotAllowedYetException exception) {

        log.error("Handling review not allowed yet exception: {}", exception.getMessage());
        return createResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(PdfImportException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePdfImportException(PdfImportException exception) {

        log.error("Handling pdf import exception: {}", exception.getMessage());
        return createResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEntityExistsException(EntityExistsException exception) {
        log.error("Handling entity exists exception: {}", exception.getMessage());
        return createResponse(HttpStatus.CONFLICT, exception);
    }

    private ErrorResponse createResponse(HttpStatusCode status, Exception exception) {

        return ErrorResponse.builder()
                .statusCode(status.value())
                .message(exception.getMessage())
                .build();
    }
}

